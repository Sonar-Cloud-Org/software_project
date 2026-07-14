package edu.najah.software.repository;

import edu.najah.software.model.Manager;

import java.util.List;
import java.util.Optional;


public interface UserRepository {

    
    List<Manager> findAll();

    
    Optional<Manager> findByUsername(String username);

    
    void save(Manager manager);
}
