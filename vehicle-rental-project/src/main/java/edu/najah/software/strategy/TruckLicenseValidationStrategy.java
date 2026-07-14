package edu.najah.software.strategy;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.Truck;
import edu.najah.software.model.Vehicle;


public class TruckLicenseValidationStrategy implements RentalValidationStrategy {

    
    @Override
    public void validate(Customer customer, Vehicle vehicle, int durationDays) throws RentalException {
        if (vehicle instanceof Truck) {
            if (!"HEAVY".equalsIgnoreCase(customer.getLicenseType())) {
                throw new RentalException("Truck rental requires a HEAVY driver's license classification.");
            }
        }
    }
}
