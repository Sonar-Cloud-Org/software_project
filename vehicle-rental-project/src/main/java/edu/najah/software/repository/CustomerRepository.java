package edu.najah.software.repository;

import edu.najah.software.model.Customer;

import java.util.List;
import java.util.Optional;


public interface CustomerRepository {

    
    List<Customer> findAll();

    
    Optional<Customer> findById(String customerId);

    
    void save(Customer customer);
}
