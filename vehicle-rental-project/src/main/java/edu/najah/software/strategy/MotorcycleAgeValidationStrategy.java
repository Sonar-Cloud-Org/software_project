package edu.najah.software.strategy;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.Motorcycle;
import edu.najah.software.model.Vehicle;


public class MotorcycleAgeValidationStrategy implements RentalValidationStrategy {

    
    @Override
    public void validate(Customer customer, Vehicle vehicle, int durationDays) throws RentalException {
        if (vehicle instanceof Motorcycle) {
            if (customer.getAge() < 21) {
                throw new RentalException("Motorcycle rental requires the customer to be at least 21 years old.");
            }
        }
    }
}
