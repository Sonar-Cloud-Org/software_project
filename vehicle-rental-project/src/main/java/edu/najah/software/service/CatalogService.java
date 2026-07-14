package edu.najah.software.service;

import edu.najah.software.exception.AuthenticationException;
import edu.najah.software.model.Vehicle;
import edu.najah.software.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;


public class CatalogService {

    
    private final VehicleRepository vehicleRepository;

    
    private final AuthenticationService authService;

    
    public CatalogService(VehicleRepository vehicleRepository, AuthenticationService authService) {
        this.vehicleRepository = vehicleRepository;
        this.authService = authService;
    }

    
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(Vehicle::isAvailable)
                .collect(Collectors.toList());
    }

    
    public List<Vehicle> getAllVehicles() throws AuthenticationException {
        authService.checkLoggedIn();
        return vehicleRepository.findAll();
    }

    
    public void addVehicle(Vehicle vehicle) throws AuthenticationException {
        authService.checkLoggedIn();
        vehicleRepository.save(vehicle);
    }
}
