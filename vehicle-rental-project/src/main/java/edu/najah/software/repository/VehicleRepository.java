package edu.najah.software.repository;

import edu.najah.software.model.Vehicle;

import java.util.List;
import java.util.Optional;


public interface VehicleRepository {

    
    List<Vehicle> findAll();

    
    Optional<Vehicle> findById(String vehicleId);

    
    void save(Vehicle vehicle);

    
    void saveAll(List<Vehicle> vehicles);
}
