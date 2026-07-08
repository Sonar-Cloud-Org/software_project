package edu.najah.software.model;

/**
 * Represents an abstract rental vehicle in the system.
 * Serves as the base class for specific vehicle types.
 */
public abstract class Vehicle {
    /**
     * The unique identifier of the vehicle.
     */
    private final String vehicleId;
    /**
     * The brand of the vehicle.
     */
    private final String brand;

    /**
     * The model name of the vehicle.
     */
    private final String model;
    /**
     * The license plate number of the vehicle.
     */
    private final String licensePlate;
    /**
     * The standard daily rental rate of the vehicle.
     */
    private final double dailyRate;
    /**
     * Flag indicating whether the vehicle is currently available for rent.
     */
    private boolean isAvailable;
    /**
     * Constructs a new Vehicle.
     *
     * @param vehicleId    the vehicle's unique ID
     * @param brand        the manufacturer or brand of the vehicle
     * @param model        the model name of the vehicle
     * @param licensePlate the license plate number
     * @param dailyRate    the standard rental cost per day
     * @param isAvailable  the starting availability status of the vehicle
     */
    protected Vehicle(String vehicleId, String brand, String model, String licensePlate, double dailyRate, boolean isAvailable) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.dailyRate = dailyRate;
        this.isAvailable = isAvailable;
    }
    /**
     * Gets the unique ID of the vehicle.
     *
     * @return the vehicle ID
     */
    public String getVehicleId() {

        return vehicleId;
    }
    /**
     * Gets the brand of the vehicle.
     *
     * @return the brand
     */
    public String getBrand() {

        return brand;
    }
    /**
     * Gets the model name of the vehicle.
     *
     * @return the model
     */
    public String getModel() {

        return model;
    }
    /**
     * Gets the license plate number of the vehicle.
     *
     * @return the license plate
     */
    public String getLicensePlate() {

        return licensePlate;
    }
    /**
     * Gets the standard daily rate for renting this vehicle.
     *
     * @return the daily rate
     */
    public double getDailyRate() {

        return dailyRate;
    }
    /**
     * Checks if the vehicle is currently available for rent.
     *
     * @return true if available, false if rented or unavailable
     */
    public boolean isAvailable() {

        return isAvailable;
    }
    /**
     * Sets the availability status of the vehicle.
     *
     * @param available the new availability status
     */
    public void setAvailable(boolean available) {

        isAvailable = available;
    }
    /**
     * Abstract method to retrieve the vehicle type identifier (e.g., "Car", "Motorcycle").
     *
     * @return the string name of the vehicle type
     */
    public abstract String getVehicleType();
}
