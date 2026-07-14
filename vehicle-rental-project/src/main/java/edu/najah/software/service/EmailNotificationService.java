package edu.najah.software.service;

import edu.najah.software.model.Customer;
import edu.najah.software.model.RentalRecord;
import edu.najah.software.model.Vehicle;
import edu.najah.software.observer.RentalObserver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class EmailNotificationService implements RentalObserver {

    
    private final List<String> sentNotifications = new ArrayList<>();

    public EmailNotificationService() {
        loadNotificationsFromFile();
    }

    private String getLogFilePath() {
        String userDir = System.getProperty("user.dir");
        File projectDir = new File(userDir);
        String dataDir = projectDir.getName().equals("vehicle-rental-project") ? "data" : "vehicle-rental-project/data";
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dataDir + "/email_reminders.txt";
    }

    private void loadNotificationsFromFile() {
        sentNotifications.clear();
        File file = new File(getLogFilePath());
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    sentNotifications.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading notifications file: " + e.getMessage());
        }
    }

    private void appendNotificationToFile(String logMsg) {
        File file = new File(getLogFilePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(logMsg);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing notification to file: " + e.getMessage());
        }
    }

    
    @Override
    public void onRentalCreated(RentalRecord record, Customer customer, Vehicle vehicle) {
        
    }

    
    @Override
    public void onRentalReturned(RentalRecord record, Customer customer, Vehicle vehicle) {
        
    }

    
    @Override
    public void onExpiryReminderSent(RentalRecord record, Customer customer, String message) {
        String logMsg = "Email sent to [" + customer.getEmail() + "]: " + message;
        
        if (sentNotifications.contains(logMsg)) {
            return;
        }
        System.out.println(logMsg);
        sentNotifications.add(logMsg);
        appendNotificationToFile(logMsg);
    }

    
    public List<String> getSentNotifications() {
        
        loadNotificationsFromFile();
        return new ArrayList<>(sentNotifications);
    }

    
    public void clearNotifications() {
        sentNotifications.clear();
        File file = new File(getLogFilePath());
        if (file.exists()) {
            file.delete();
        }
    }
}
