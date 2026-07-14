package edu.najah.software.strategy;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.Vehicle;


public class DurationValidationStrategy implements RentalValidationStrategy {

    
    @Override
    public void validate(Customer customer, Vehicle vehicle, int durationDays) throws RentalException {
        if (durationDays < 1) {
            throw new RentalException("Rental duration must be at least 1 day.");
        }
        if (durationDays > 30) {
            throw new RentalException("Rental duration cannot exceed 30 days.");
        }
    }
}
