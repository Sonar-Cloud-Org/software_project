package edu.najah.software.model;

public class ElectricVehicle extends Vehicle {

    private double batteryPercentage;

    public ElectricVehicle(String vehicleId, String brand, String model, String licensePlate, double dailyRate, boolean isAvailable, double batteryPercentage) {
        super(vehicleId, brand, model, licensePlate, dailyRate, isAvailable);
        this.batteryPercentage = batteryPercentage;
    }

    public double getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(double batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getVehicleType() {
        return "ElectricVehicle";
    }
}
