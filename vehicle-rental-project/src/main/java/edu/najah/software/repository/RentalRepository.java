package edu.najah.software.repository;

import edu.najah.software.model.RentalRecord;

import java.util.List;
import java.util.Optional;


public interface RentalRepository {

    
    List<RentalRecord> findAll();

    
    Optional<RentalRecord> findById(String rentalId);

    
    void save(RentalRecord record);
}
