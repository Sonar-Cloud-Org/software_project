package edu.najah.software.repository;

import edu.najah.software.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FileVehicleRepository implements VehicleRepository {

    
    private final String filePath;

    
    public FileVehicleRepository() {
        this("data");
    }

    
    public FileVehicleRepository(String dataDir) {
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            
        }
        this.filePath = dataDir + File.separator + "vehicles.txt";
        initializeDefaultData();
    }

    
    private void initializeDefaultData() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            List<Vehicle> defaults = new ArrayList<>();
            defaults.add(new Car("V001", "Toyota", "Corolla", "ABC-123", 50.0, true));
            defaults.add(new Motorcycle("V002", "Yamaha", "YZF-R3", "MTC-999", 40.0, true));
            defaults.add(new Van("V003", "Ford", "Transit", "VAN-456", 80.0, true));
            defaults.add(new Truck("V004", "Volvo", "FH16", "TRK-789", 150.0, true));
            defaults.add(new ElectricVehicle("V005", "Tesla", "Model 3", "EV-111", 100.0, true, 85.0));
            writeAll(defaults);
        }
    }

    
    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return vehicles;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    String type = parts[0];
                    String id = parts[1];
                    String brand = parts[2];
                    String model = parts[3];
                    String licensePlate = parts[4];
                    double dailyRate = Double.parseDouble(parts[5]);
                    boolean isAvailable = Boolean.parseBoolean(parts[6]);

                    switch (type) {
                        case "Car":
                            vehicles.add(new Car(id, brand, model, licensePlate, dailyRate, isAvailable));
                            break;
                        case "Motorcycle":
                            vehicles.add(new Motorcycle(id, brand, model, licensePlate, dailyRate, isAvailable));
                            break;
                        case "Van":
                            vehicles.add(new Van(id, brand, model, licensePlate, dailyRate, isAvailable));
                            break;
                        case "Truck":
                            vehicles.add(new Truck(id, brand, model, licensePlate, dailyRate, isAvailable));
                            break;
                        case "ElectricVehicle":
                            double battery = parts.length >= 8 ? Double.parseDouble(parts[7]) : 100.0;
                            vehicles.add(new ElectricVehicle(id, brand, model, licensePlate, dailyRate, isAvailable, battery));
                            break;
                        default:
                            
                            break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            
        }
        return vehicles;
    }

    
    @Override
    public Optional<Vehicle> findById(String vehicleId) {
        return findAll().stream()
                .filter(v -> v.getVehicleId().equalsIgnoreCase(vehicleId))
                .findFirst();
    }

    
    @Override
    public void save(Vehicle vehicle) {
        List<Vehicle> vehicles = findAll();
        vehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicle.getVehicleId()));
        vehicles.add(vehicle);
        writeAll(vehicles);
    }

    
    @Override
    public void saveAll(List<Vehicle> vehicles) {
        List<Vehicle> existing = findAll();
        for (Vehicle v : vehicles) {
            existing.removeIf(e -> e.getVehicleId().equalsIgnoreCase(v.getVehicleId()));
            existing.add(v);
        }
        writeAll(existing);
    }

    
    private void writeAll(List<Vehicle> vehicles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Vehicle v : vehicles) {
                StringBuilder sb = new StringBuilder();
                sb.append(v.getVehicleType()).append("|")
                  .append(v.getVehicleId()).append("|")
                  .append(v.getBrand()).append("|")
                  .append(v.getModel()).append("|")
                  .append(v.getLicensePlate()).append("|")
                  .append(v.getDailyRate()).append("|")
                  .append(v.isAvailable());

                if (v instanceof ElectricVehicle) {
                    sb.append("|").append(((ElectricVehicle) v).getBatteryPercentage());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            
        }
    }
}
