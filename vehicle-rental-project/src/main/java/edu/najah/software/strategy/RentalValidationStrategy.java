package edu.najah.software.strategy;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.Vehicle;


public interface RentalValidationStrategy {

    
    void validate(Customer customer, Vehicle vehicle, int durationDays) throws RentalException;
}
