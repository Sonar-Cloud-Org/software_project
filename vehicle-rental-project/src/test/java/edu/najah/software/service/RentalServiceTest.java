package edu.najah.software.service;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.*;
import edu.najah.software.observer.RentalObserver;
import edu.najah.software.repository.*;
import edu.najah.software.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class RentalServiceTest {

    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private CustomerRepository customerRepository;
    private DateTimeService dateTimeService;
    private RentalPricingStrategy pricingStrategy;
    private RentalObserver rentalObserver;
    private RentalService rentalService;

    private Customer standardCustomer;
    private Customer youngCustomer;
    private Customer heavyLicenseCustomer;

    private Car car;
    private Motorcycle motorcycle;
    private Truck truck;
    private ElectricVehicle healthyEV;
    private ElectricVehicle lowBatteryEV;

    @BeforeEach
    public void setUp() {
        
        rentalRepository = mock(RentalRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        customerRepository = mock(CustomerRepository.class);
        dateTimeService = mock(DateTimeService.class);
        rentalObserver = mock(RentalObserver.class);

        
        pricingStrategy = new DefaultRentalPricingStrategy();

        
        List<RentalValidationStrategy> strategies = new ArrayList<>();
        strategies.add(new DurationValidationStrategy());
        strategies.add(new MotorcycleAgeValidationStrategy());
        strategies.add(new TruckLicenseValidationStrategy());
        strategies.add(new ElectricVehicleBatteryValidationStrategy());

        
        rentalService = new RentalService(
                rentalRepository,
                vehicleRepository,
                customerRepository,
                dateTimeService,
                pricingStrategy,
                strategies
        );

        
        rentalService.addObserver(rentalObserver);

        
        standardCustomer = new Customer("C001", "John Doe", "john@example.com", 25, "REGULAR", "customer123");
        youngCustomer = new Customer("C002", "Jane Smith", "jane@example.com", 19, "REGULAR", "customer123");
        heavyLicenseCustomer = new Customer("C003", "Heavy Driver", "heavy@example.com", 35, "HEAVY", "customer123");

        car = new Car("V001", "Toyota", "Corolla", "ABC-123", 50.0, true);
        motorcycle = new Motorcycle("V002", "Yamaha", "R1", "XYZ-789", 40.0, true);
        truck = new Truck("V003", "Volvo", "FH16", "TRK-789", 100.0, true);
        healthyEV = new ElectricVehicle("V004", "Tesla", "Model 3", "EV-111", 80.0, true, 85.0);
        lowBatteryEV = new ElectricVehicle("V005", "Tesla", "Model Y", "EV-222", 80.0, true, 15.0);

        
        when(customerRepository.findById("C001")).thenReturn(Optional.of(standardCustomer));
        when(customerRepository.findById("C002")).thenReturn(Optional.of(youngCustomer));
        when(customerRepository.findById("C003")).thenReturn(Optional.of(heavyLicenseCustomer));

        when(vehicleRepository.findById("V001")).thenReturn(Optional.of(car));
        when(vehicleRepository.findById("V002")).thenReturn(Optional.of(motorcycle));
        when(vehicleRepository.findById("V003")).thenReturn(Optional.of(truck));
        when(vehicleRepository.findById("V004")).thenReturn(Optional.of(healthyEV));
        when(vehicleRepository.findById("V005")).thenReturn(Optional.of(lowBatteryEV));

        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 8));
    }

    @Test
    public void testRentVehicleSuccess() throws RentalException {
        RentalRecord record = rentalService.rentVehicle("C001", "V001", 5);

        assertNotNull(record);
        assertEquals("C001", record.getCustomerId());
        assertEquals("V001", record.getVehicleId());
        assertEquals(LocalDate.of(2026, 7, 8), record.getRentalDate());
        assertEquals(LocalDate.of(2026, 7, 13), record.getExpectedReturnDate());
        assertFalse(car.isAvailable());

        verify(rentalRepository, times(1)).save(record);
        verify(vehicleRepository, times(1)).save(car);
        verify(rentalObserver, times(1)).onRentalCreated(record, standardCustomer, car);
    }

    @Test
    public void testPreventDoubleBooking() {
        car.setAvailable(false);

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C001", "V001", 5);
        });

        assertTrue(exception.getMessage().contains("already rented"));
        verify(rentalRepository, never()).save(any());
    }

    @Test
    public void testEnforceRentalDurationLimitsMin() {
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C001", "V001", 0);
        });

        assertTrue(exception.getMessage().contains("must be at least 1 day"));
    }

    @Test
    public void testEnforceRentalDurationLimitsMax() {
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C001", "V001", 31);
        });

        assertTrue(exception.getMessage().contains("cannot exceed 30 days"));
    }

    @Test
    public void testMotorcycleAgeRestrictionUnderAge() {
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C002", "V002", 5);
        });

        assertTrue(exception.getMessage().contains("at least 21 years old"));
    }

    @Test
    public void testMotorcycleAgeRestrictionAllowed() throws RentalException {
        RentalRecord record = rentalService.rentVehicle("C001", "V002", 5);
        assertNotNull(record);
    }

    @Test
    public void testTruckLicenseValidationUnauthorized() {
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C001", "V003", 5);
        });

        assertTrue(exception.getMessage().contains("requires a HEAVY driver's license"));
    }

    @Test
    public void testTruckLicenseValidationAuthorized() throws RentalException {
        RentalRecord record = rentalService.rentVehicle("C003", "V003", 5);
        assertNotNull(record);
    }

    @Test
    public void testElectricVehicleBatteryCheckLow() {
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentVehicle("C001", "V005", 5);
        });

        assertTrue(exception.getMessage().contains("battery level is too low"));
    }

    @Test
    public void testElectricVehicleBatteryCheckAllowed() throws RentalException {
        RentalRecord record = rentalService.rentVehicle("C001", "V004", 5);
        assertNotNull(record);
    }

    @Test
    public void testReturnVehicleOnTime() throws RentalException {
        RentalRecord record = new RentalRecord("R001", "C001", "V001", 
                LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 13));
        car.setAvailable(false);

        when(rentalRepository.findById("R001")).thenReturn(Optional.of(record));
        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 13)); 

        RentalRecord updated = rentalService.returnVehicle("R001");

        assertTrue(updated.isClosed());
        assertEquals(LocalDate.of(2026, 7, 13), updated.getActualReturnDate());
        assertEquals(250.0, updated.getTotalCost()); 
        assertTrue(car.isAvailable());

        verify(rentalRepository, times(1)).save(updated);
        verify(vehicleRepository, times(1)).save(car);
        verify(rentalObserver, times(1)).onRentalReturned(updated, standardCustomer, car);
    }

    @Test
    public void testReturnVehicleLateWithPenalty() throws RentalException {
        RentalRecord record = new RentalRecord("R001", "C001", "V001", 
                LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 13));
        car.setAvailable(false);

        when(rentalRepository.findById("R001")).thenReturn(Optional.of(record));
        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 15)); 

        RentalRecord updated = rentalService.returnVehicle("R001");

        assertTrue(updated.isClosed());
        assertEquals(LocalDate.of(2026, 7, 15), updated.getActualReturnDate());
        
        
        assertEquals(400.0, updated.getTotalCost());
        assertTrue(car.isAvailable());
    }

    @Test
    public void testRentalExpiryWarningNotOverdue() {
        RentalRecord record = new RentalRecord("R001", "C001", "V001", 
                LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 13));
        
        List<RentalRecord> records = new ArrayList<>();
        records.add(record);
        
        when(rentalRepository.findAll()).thenReturn(records);
        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 10)); 

        rentalService.generateExpiryReminders();

        verify(rentalObserver, never()).onExpiryReminderSent(any(), any(), any());
    }

    @Test
    public void testRentalExpiryWarningDueToday() {
        RentalRecord record = new RentalRecord("R001", "C001", "V001", 
                LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 13));
        
        List<RentalRecord> records = new ArrayList<>();
        records.add(record);
        
        when(rentalRepository.findAll()).thenReturn(records);
        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 13)); 

        rentalService.generateExpiryReminders();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rentalObserver, times(1)).onExpiryReminderSent(eq(record), eq(standardCustomer), messageCaptor.capture());
        assertTrue(messageCaptor.getValue().contains("due today"));
    }

    @Test
    public void testRentalExpiryWarningOverdue() {
        RentalRecord record = new RentalRecord("R001", "C001", "V001", 
                LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 13));
        
        List<RentalRecord> records = new ArrayList<>();
        records.add(record);
        
        when(rentalRepository.findAll()).thenReturn(records);
        when(dateTimeService.getCurrentDate()).thenReturn(LocalDate.of(2026, 7, 15)); 

        rentalService.generateExpiryReminders();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rentalObserver, times(1)).onExpiryReminderSent(eq(record), eq(standardCustomer), messageCaptor.capture());
        assertTrue(messageCaptor.getValue().contains("OVERDUE by 2 day"));
    }
}
