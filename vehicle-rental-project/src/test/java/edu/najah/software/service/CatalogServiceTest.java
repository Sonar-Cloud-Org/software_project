package edu.najah.software.service;

import edu.najah.software.exception.AuthenticationException;
import edu.najah.software.model.Car;
import edu.najah.software.model.Vehicle;
import edu.najah.software.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CatalogServiceTest {

    private VehicleRepository vehicleRepository;
    private AuthenticationService authService;
    private CatalogService catalogService;

    private Car carAvailable;
    private Car carRented;

    @BeforeEach
    public void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        authService = mock(AuthenticationService.class);
        catalogService = new CatalogService(vehicleRepository, authService);

        carAvailable = new Car("V001", "Toyota", "Corolla", "ABC-123", 50.0, true);
        carRented = new Car("V002", "Honda", "Civic", "XYZ-789", 60.0, false);
    }

    @Test
    public void testGetAvailableVehiclesFiltersCorrectly() {
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(carAvailable, carRented));

        List<Vehicle> available = catalogService.getAvailableVehicles();

        assertEquals(1, available.size());
        assertEquals("V001", available.get(0).getVehicleId());
        assertTrue(available.get(0).isAvailable());
    }

    @Test
    public void testGetAllVehiclesWhenLoggedIn() throws AuthenticationException {
        
        doNothing().when(authService).checkLoggedIn();
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(carAvailable, carRented));

        List<Vehicle> all = catalogService.getAllVehicles();

        assertEquals(2, all.size());
        verify(authService, times(1)).checkLoggedIn();
    }

    @Test
    public void testGetAllVehiclesThrowsWhenLoggedOut() throws AuthenticationException {
        doThrow(new AuthenticationException("Access denied")).when(authService).checkLoggedIn();

        assertThrows(AuthenticationException.class, () -> {
            catalogService.getAllVehicles();
        });
    }

    @Test
    public void testAddVehicleWhenLoggedIn() throws AuthenticationException {
        doNothing().when(authService).checkLoggedIn();

        catalogService.addVehicle(carAvailable);

        verify(vehicleRepository, times(1)).save(carAvailable);
        verify(authService, times(1)).checkLoggedIn();
    }

    @Test
    public void testAddVehicleThrowsWhenLoggedOut() throws AuthenticationException {
        doThrow(new AuthenticationException("Access denied")).when(authService).checkLoggedIn();

        assertThrows(AuthenticationException.class, () -> {
            catalogService.addVehicle(carAvailable);
        });

        verify(vehicleRepository, never()).save(any());
    }
}
