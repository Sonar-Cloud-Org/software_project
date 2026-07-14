package edu.najah.software.service;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.RentalRecord;
import edu.najah.software.model.Vehicle;
import edu.najah.software.observer.RentalObserver;
import edu.najah.software.repository.CustomerRepository;
import edu.najah.software.repository.RentalRepository;
import edu.najah.software.repository.VehicleRepository;
import edu.najah.software.strategy.RentalPricingStrategy;
import edu.najah.software.strategy.RentalValidationStrategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final DateTimeService dateTimeService;
    private final RentalPricingStrategy pricingStrategy;
    private final List<RentalValidationStrategy> validationStrategies;
    private final List<RentalObserver> observers = new ArrayList<>();

    
    public RentalService(RentalRepository rentalRepository,
                         VehicleRepository vehicleRepository,
                         CustomerRepository customerRepository,
                         DateTimeService dateTimeService,
                         RentalPricingStrategy pricingStrategy,
                         List<RentalValidationStrategy> validationStrategies) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.dateTimeService = dateTimeService;
        this.pricingStrategy = pricingStrategy;
        this.validationStrategies = validationStrategies;
    }

    
    public void addObserver(RentalObserver observer) {
        observers.add(observer);
    }

    
    public void removeObserver(RentalObserver observer) {
        observers.remove(observer);
    }

    
    public RentalRecord rentVehicle(String customerId, String vehicleId, int durationDays) throws RentalException {
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RentalException("Customer not found with ID: " + customerId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RentalException("Vehicle not found with ID: " + vehicleId));

        
        if (!vehicle.isAvailable()) {
            throw new RentalException("Vehicle " + vehicleId + " is already rented (Double Booking prevented).");
        }

        
        for (RentalValidationStrategy strategy : validationStrategies) {
            strategy.validate(customer, vehicle, durationDays);
        }

        
        LocalDate startDate = dateTimeService.getCurrentDate();
        LocalDate expectedEndDate = startDate.plusDays(durationDays);
        String rentalId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        RentalRecord record = new RentalRecord(rentalId, customerId, vehicleId, startDate, expectedEndDate);
        
        
        vehicle.setAvailable(false);

        
        rentalRepository.save(record);
        vehicleRepository.save(vehicle);

        
        for (RentalObserver observer : observers) {
            observer.onRentalCreated(record, customer, vehicle);
        }

        return record;
    }

    
    public RentalRecord returnVehicle(String rentalId) throws RentalException {
        
        RentalRecord record = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalException("Rental record not found with ID: " + rentalId));

        if (record.isClosed()) {
            throw new RentalException("Rental transaction " + rentalId + " is already completed and closed.");
        }

        
        Vehicle vehicle = vehicleRepository.findById(record.getVehicleId())
                .orElseThrow(() -> new RentalException("Vehicle not found for ID: " + record.getVehicleId()));
        Customer customer = customerRepository.findById(record.getCustomerId())
                .orElseThrow(() -> new RentalException("Customer not found for ID: " + record.getCustomerId()));

        
        LocalDate returnDate = dateTimeService.getCurrentDate();
        record.setActualReturnDate(returnDate);

        long targetDuration = ChronoUnit.DAYS.between(record.getRentalDate(), record.getExpectedReturnDate());
        long actualDuration = ChronoUnit.DAYS.between(record.getRentalDate(), returnDate);

        double baseCost;
        long lateDays = 0;

        if (actualDuration <= targetDuration) {
            
            
            
            
            
            baseCost = pricingStrategy.calculateCost(vehicle, Math.max(0, actualDuration));
        } else {
            baseCost = pricingStrategy.calculateCost(vehicle, targetDuration);
            lateDays = actualDuration - targetDuration;
        }

        double latePenalty = pricingStrategy.calculateLatePenalty(vehicle, lateDays);
        record.setTotalCost(baseCost + latePenalty);
        record.setClosed(true);

        
        vehicle.setAvailable(true);

        
        rentalRepository.save(record);
        vehicleRepository.save(vehicle);

        
        for (RentalObserver observer : observers) {
            observer.onRentalReturned(record, customer, vehicle);
        }

        return record;
    }

    
    public void generateExpiryReminders() {
        LocalDate currentDate = dateTimeService.getCurrentDate();
        List<RentalRecord> records = rentalRepository.findAll();

        for (RentalRecord r : records) {
            if (!r.isClosed()) {
                
                if (!currentDate.isBefore(r.getExpectedReturnDate())) {
                    Optional<Customer> customerOpt = customerRepository.findById(r.getCustomerId());
                    if (customerOpt.isPresent()) {
                        Customer customer = customerOpt.get();
                        
                        long diff = ChronoUnit.DAYS.between(r.getExpectedReturnDate(), currentDate);
                        String message;
                        if (diff == 0) {
                            message = "Reminder: Your rental for vehicle " + r.getVehicleId() + " is due today (" + r.getExpectedReturnDate() + ").";
                        } else {
                            message = "Urgent: Your rental for vehicle " + r.getVehicleId() + " is OVERDUE by " + diff + " day(s).";
                        }

                        for (RentalObserver observer : observers) {
                            observer.onExpiryReminderSent(r, customer, message);
                        }
                    }
                }
            }
        }
    }
}
