package edu.najah.software.repository;

import edu.najah.software.model.RentalRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FileRentalRepository implements RentalRepository {

    
    private final String filePath;

    
    public FileRentalRepository() {
        this("data");
    }

    
    public FileRentalRepository(String dataDir) {
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            
        }
        this.filePath = dataDir + File.separator + "rentals.txt";
    }

    
    @Override
    public List<RentalRecord> findAll() {
        List<RentalRecord> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String rentalId = parts[0];
                    String customerId = parts[1];
                    String vehicleId = parts[2];
                    LocalDate rentalDate = LocalDate.parse(parts[3]);
                    LocalDate expectedReturnDate = LocalDate.parse(parts[4]);
                    
                    LocalDate actualReturnDate = null;
                    if (!parts[5].equalsIgnoreCase("null")) {
                        actualReturnDate = LocalDate.parse(parts[5]);
                    }
                    
                    double totalCost = Double.parseDouble(parts[6]);
                    boolean isClosed = Boolean.parseBoolean(parts[7]);

                    RentalRecord record = new RentalRecord(rentalId, customerId, vehicleId, rentalDate, expectedReturnDate);
                    record.setActualReturnDate(actualReturnDate);
                    record.setTotalCost(totalCost);
                    record.setClosed(isClosed);

                    records.add(record);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            
        }
        return records;
    }

    
    @Override
    public Optional<RentalRecord> findById(String rentalId) {
        return findAll().stream()
                .filter(r -> r.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst();
    }

    
    @Override
    public void save(RentalRecord record) {
        List<RentalRecord> records = findAll();
        records.removeIf(r -> r.getRentalId().equalsIgnoreCase(record.getRentalId()));
        records.add(record);
        writeAll(records);
    }

    
    private void writeAll(List<RentalRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (RentalRecord r : records) {
                String actualReturn = r.getActualReturnDate() != null ? r.getActualReturnDate().toString() : "null";
                writer.write(r.getRentalId() + "|" +
                        r.getCustomerId() + "|" +
                        r.getVehicleId() + "|" +
                        r.getRentalDate() + "|" +
                        r.getExpectedReturnDate() + "|" +
                        actualReturn + "|" +
                        r.getTotalCost() + "|" +
                        r.isClosed());
                writer.newLine();
            }
        } catch (IOException e) {
            
        }
    }
}
