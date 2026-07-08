package edu.najah.software.model;

public class Car extends Vehicle {

    public Car(String vehicleId, String brand, String model, String licensePlate, double dailyRate, boolean isAvailable) {
        super(vehicleId, brand, model, licensePlate, dailyRate, isAvailable);
    }

    public String getVehicleType() {
        return "Car";
    }
}
