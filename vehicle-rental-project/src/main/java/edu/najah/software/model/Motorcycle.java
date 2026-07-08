package edu.najah.software.model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String vehicleId, String brand, String model, String licensePlate, double dailyRate, boolean isAvailable) {
        super(vehicleId, brand, model, licensePlate, dailyRate, isAvailable);
    }

    public String getVehicleType() {
        return "Motorcycle";
    }
}
