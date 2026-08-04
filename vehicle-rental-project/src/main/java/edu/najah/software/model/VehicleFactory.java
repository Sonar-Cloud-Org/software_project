package edu.najah.software.model;

public final class VehicleFactory {

    private VehicleFactory() {
    }

    public static Vehicle create(
            String type,
            String id,
            String brand,
            String model,
            String plate,
            double dailyRate,
            boolean available,
            double batteryPercentage) {

        switch (type) {
            case "Car":
                return new Car(
                        id, brand, model, plate, dailyRate, available
                );

            case "Motorcycle":
                return new Motorcycle(
                        id, brand, model, plate, dailyRate, available
                );

            case "Van":
                return new Van(
                        id, brand, model, plate, dailyRate, available
                );

            case "Truck":
                return new Truck(
                        id, brand, model, plate, dailyRate, available
                );

            case "ElectricVehicle":
                return new ElectricVehicle(
                        id,
                        brand,
                        model,
                        plate,
                        dailyRate,
                        available,
                        batteryPercentage
                );

            default:
                throw new IllegalArgumentException(
                        "Unsupported vehicle type: " + type
                );
        }
    }
}