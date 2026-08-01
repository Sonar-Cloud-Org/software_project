package edu.najah.software.repository;

import edu.najah.software.model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FileCustomerRepository implements CustomerRepository {

    
    private final String filePath;

    
    public FileCustomerRepository() {
        this("data");
    }

    
    public FileCustomerRepository(String dataDir) {
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            
        }
        this.filePath = dataDir + File.separator + "customers.txt";
        initializeDefaultData();
    }

    
    private void initializeDefaultData() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            List<Customer> defaults = new ArrayList<>();
            defaults.add(new Customer("C001", "John Doe", "john@example.com", 25, "REGULAR", "customer123"));
            defaults.add(new Customer("C002", "Jane Smith", "jane@example.com", 19, "REGULAR", "customer123"));
            defaults.add(new Customer("C003", "Heavy Driver", "heavy@example.com", 35, "HEAVY", "customer123"));
            defaults.add(new Customer("C004", "Alice Cooper", "alice@example.com", 30, "REGULAR", "customer123"));
            writeAll(defaults);
        }
    }

    
    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    int age = Integer.parseInt(parts[3]);
                    String licenseType = parts[4];
                    String password = parts.length >= 6 ? parts[5] : "customer123";
                    
                    customers.add(new Customer(id, name, email, age, licenseType, password));
                }
            }
        } catch (IOException | NumberFormatException e) {
            
        }
        return customers;
    }

    
    @Override
    public Optional<Customer> findById(String customerId) {
        return findAll().stream()
                .filter(c -> c.getCustomerId().equalsIgnoreCase(customerId))
                .findFirst();
    }

    
    @Override
    public void save(Customer customer) {
        List<Customer> customers = findAll();
        customers.removeIf(c -> c.getCustomerId().equalsIgnoreCase(customer.getCustomerId()));
        customers.add(customer);
        writeAll(customers);
    }

    
    private void writeAll(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Customer c : customers) {
                writer.write(c.getCustomerId() + "|" +
                        c.getName() + "|" +
                        c.getEmail() + "|" +
                        c.getAge() + "|" +
                        c.getLicenseType() + "|" +
                        c.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            
        }
    }
}
