package edu.najah.software.strategy;

import edu.najah.software.model.Vehicle;


public interface RentalPricingStrategy {

    
    double calculateCost(Vehicle vehicle, long durationDays);

    double calculateLatePenalty(Vehicle vehicle, long lateDays);
}
