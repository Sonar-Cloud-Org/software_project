package edu.najah.software.strategy;

import edu.najah.software.exception.RentalException;
import edu.najah.software.model.Customer;
import edu.najah.software.model.ElectricVehicle;
import edu.najah.software.model.Vehicle;


public class ElectricVehicleBatteryValidationStrategy implements RentalValidationStrategy {

    
    @Override
    public void validate(Customer customer, Vehicle vehicle, int durationDays) throws RentalException {
        if (vehicle instanceof ElectricVehicle) {
            ElectricVehicle ev = (ElectricVehicle) vehicle;
            if (ev.getBatteryPercentage() < 20.0) {
                throw new RentalException("Electric vehicle battery level is too low (" + 
                        ev.getBatteryPercentage() + "%). Must be at least 20% to rent.");
            }
        }
    }
}
