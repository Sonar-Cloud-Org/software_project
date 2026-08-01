package edu.najah.software.strategy;

import edu.najah.software.model.Vehicle;


public class DefaultRentalPricingStrategy implements RentalPricingStrategy {

    
    @Override
    public double calculateCost(Vehicle vehicle, long durationDays) {
        if (durationDays <= 0) {
            return 0.0;
        }
        return vehicle.getDailyRate() * durationDays;
    }

    
    @Override
    public double calculateLatePenalty(Vehicle vehicle, long lateDays) {
        if (lateDays <= 0) {
            return 0.0;
        }
        return vehicle.getDailyRate() * 1.5 * lateDays;
    }
}
