package edu.najah.software.repository;

import edu.najah.software.model.Manager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FileUserRepository implements UserRepository {

    
    private final String filePath;

    
    public FileUserRepository() {
        this("data");
    }

    
    public FileUserRepository(String dataDir) {
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to create the data directory.",
                    e
            );
        }
        this.filePath = dataDir + File.separator + "users.txt";
        initializeDefaultData();
    }

    
    private void initializeDefaultData() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            save(new Manager("admin", "admin123"));
        }
    }

    
    @Override
    public List<Manager> findAll() {
        List<Manager> managers = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return managers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    managers.add(new Manager(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read users from file.",
                    e
            );
        }
        return managers;
    }

    
    @Override
    public Optional<Manager> findByUsername(String username) {
        return findAll().stream()
                .filter(m -> m.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    
    @Override
    public void save(Manager manager) {
        List<Manager> managers = findAll();
        managers.removeIf(m -> m.getUsername().equalsIgnoreCase(manager.getUsername()));
        managers.add(manager);
        writeAll(managers);
    }

    
    private void writeAll(List<Manager> managers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Manager m : managers) {
                writer.write(m.getUsername() + "|" + m.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to save users to file.",
                    e
            );
        }
    }
}
