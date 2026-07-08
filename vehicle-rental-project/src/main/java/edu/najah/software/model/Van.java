package edu.najah.software.model;

public class Van extends Vehicle {

    public Van(String vehicleId, String brand, String model, String licensePlate, double dailyRate, boolean isAvailable) {
        super(vehicleId, brand, model, licensePlate, dailyRate, isAvailable);
    }

    public String getVehicleType() {
        return "Van";
    }
}
