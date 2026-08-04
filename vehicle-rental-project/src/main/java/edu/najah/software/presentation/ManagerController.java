package edu.najah.software.presentation;

import edu.najah.software.model.*;
import edu.najah.software.repository.*;
import edu.najah.software.service.*;
import edu.najah.software.observer.RentalObserver;
import edu.najah.software.exception.RentalException;
import edu.najah.software.exception.AuthenticationException;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.UnaryOperator;


public class ManagerController implements RentalObserver {

    private MainApp mainApp;
    private AuthenticationService authService;
    private CatalogService catalogService;
    private RentalService rentalService;
    private EmailNotificationService notificationService;

    private VehicleRepository vehicleRepo;
    private CustomerRepository customerRepo;
    private RentalRepository rentalRepo;

    
    @FXML
    private TableView<Vehicle> vehiclesTable;
    @FXML
    private TableColumn<Vehicle, String> colVehId;
    @FXML
    private TableColumn<Vehicle, String> colVehType;
    @FXML
    private TableColumn<Vehicle, String> colVehBrand;
    @FXML
    private TableColumn<Vehicle, String> colVehModel;
    @FXML
    private TableColumn<Vehicle, String> colVehPlate;
    @FXML
    private TableColumn<Vehicle, Double> colVehRate;
    @FXML
    private TableColumn<Vehicle, String> colVehStatus;
    @FXML
    private TableColumn<Vehicle, String> colVehExtra;

    
    @FXML
    private ComboBox<String> vehTypeCombo;
    @FXML
    private TextField vehIdField;
    @FXML
    private TextField vehBrandField;
    @FXML
    private TextField vehModelField;
    @FXML
    private TextField vehPlateField;
    @FXML
    private TextField vehRateField;
    @FXML
    private TextField vehBattField;

    
    @FXML
    private TableView<RentalRecord> rentalsTable;
    @FXML
    private TableColumn<RentalRecord, String> colRentId;
    @FXML
    private TableColumn<RentalRecord, String> colRentCustId;
    @FXML
    private TableColumn<RentalRecord, String> colRentVehId;
    @FXML
    private TableColumn<RentalRecord, String> colRentStart;
    @FXML
    private TableColumn<RentalRecord, String> colRentDue;
    @FXML
    private TableColumn<RentalRecord, String> colRentReturned;
    @FXML
    private TableColumn<RentalRecord, String> colRentCost;
    @FXML
    private TableColumn<RentalRecord, String> colRentStatus;

    
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> colCustId;
    @FXML
    private TableColumn<Customer, String> colCustName;
    @FXML
    private TableColumn<Customer, String> colCustEmail;
    @FXML
    private TableColumn<Customer, Integer> colCustAge;
    @FXML
    private TableColumn<Customer, String> colCustLicense;

    
    @FXML
    private TextArea logsArea;

    
    public void init(MainApp mainApp,
                     AuthenticationService authService,
                     CatalogService catalogService,
                     RentalService rentalService,
                     EmailNotificationService notificationService,
                     UserRepository userRepo,
                     VehicleRepository vehicleRepo,
                     CustomerRepository customerRepo,
                     RentalRepository rentalRepo) {
        this.mainApp = mainApp;
        this.authService = authService;
        this.catalogService = catalogService;
        this.rentalService = rentalService;
        this.notificationService = notificationService;
        this.vehicleRepo = vehicleRepo;
        this.customerRepo = customerRepo;
        this.rentalRepo = rentalRepo;

        
        vehTypeCombo.getItems().addAll("Car", "Motorcycle", "Van", "Truck", "ElectricVehicle");
        vehTypeCombo.setValue("Car");

        
        colVehId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleId()));
        colVehType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleType()));
        colVehBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        colVehModel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModel()));
        colVehPlate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLicensePlate()));
        colVehRate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDailyRate()));
        colVehStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isAvailable() ? "Available" : "Rented"));
        colVehExtra.setCellValueFactory(data -> {
            Vehicle v = data.getValue();
            if (v instanceof ElectricVehicle) {
                return new SimpleStringProperty(((ElectricVehicle) v).getBatteryPercentage() + "% Battery");
            }
            return new SimpleStringProperty("-");
        });

        
        colRentId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRentalId()));
        colRentCustId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerId()));
        colRentVehId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleId()));
        colRentStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRentalDate().toString()));
        colRentDue.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpectedReturnDate().toString()));
        colRentReturned.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActualReturnDate() != null ? data.getValue().getActualReturnDate().toString() : "-"));
        colRentCost.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTotalCost() > 0 ? String.format("$%.2f", data.getValue().getTotalCost()) : "-"));
        colRentStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isClosed() ? "Closed" : "Active"));

        
        colCustId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerId()));
        colCustName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCustEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colCustAge.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAge()));
        colCustLicense.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLicenseType()));

        
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };
        vehRateField.setTextFormatter(new TextFormatter<>(decimalFilter));
        vehBattField.setTextFormatter(new TextFormatter<>(decimalFilter));

        loadActivityLog();
        refreshAllTables();
        appendLog("Manager Dashboard Initialized.");

        
        new Thread(() -> {
            try {
                Thread.sleep(800); 
            } catch (InterruptedException ignored) {}
            rentalService.generateExpiryReminders();
        }).start();
    }

    private void refreshAllTables() {
        vehiclesTable.setItems(FXCollections.observableArrayList(vehicleRepo.findAll()));
        rentalsTable.setItems(FXCollections.observableArrayList(rentalRepo.findAll()));
        customersTable.setItems(FXCollections.observableArrayList(customerRepo.findAll()));
    }


    private String requiredText(TextField field, String fieldName) {
        String value = field.getText();

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value.trim();
    }


    private double parsePositiveDouble(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        try {
            double number = Double.parseDouble(value.trim());

            if (number <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than zero."
                );
            }

            return number;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number."
            );
        }
    }


    private Vehicle createVehicleFromForm() {
        String type = vehTypeCombo.getValue();
        String id = requiredText(vehIdField, "Vehicle ID");
        String brand = requiredText(vehBrandField, "Brand");
        String model = requiredText(vehModelField, "Model");
        String plate = requiredText(vehPlateField, "License plate");

        double rate = parsePositiveDouble(
                vehRateField.getText(),
                "Daily rate"
        );

        return VehicleFactory.create(
                type,
                id,
                brand,
                model,
                plate,
                rate,
                true,
                parsePositiveDouble(
                        vehBattField.getText(),
                        "Battery percentage"
                )
        );
    }


    private void clearVehicleForm() {
        vehIdField.clear();
        vehBrandField.clear();
        vehModelField.clear();
        vehPlateField.clear();
        vehRateField.clear();
        vehBattField.setText("100");
    }


    @FXML
    private void handleSaveVehicle(ActionEvent event) {
        try {
            Vehicle vehicle = createVehicleFromForm();

            catalogService.addVehicle(vehicle);

            appendLog("Vehicle Registered: "
                    + vehicle.getVehicleId()
                    + " | Type: "
                    + vehicle.getVehicleType());

            AlertHelper.show(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Vehicle registered successfully."
            );

            clearVehicleForm();
            refreshAllTables();

        } catch (IllegalArgumentException e) {
            AlertHelper.show(
                    Alert.AlertType.ERROR,
                    "Invalid Vehicle Data",
                    e.getMessage()
            );

        } catch (AuthenticationException e) {
            AlertHelper.show(
                    Alert.AlertType.ERROR,
                    "Access Denied",
                    e.getMessage()
            );
        }
    }

    
    @FXML
    private void handleProcessReturn(ActionEvent event) {
        RentalRecord selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.show(Alert.AlertType.WARNING, "No Selection", "Please select an active rental transaction from the table.");
            return;
        }

        if (selected.isClosed()) {
            AlertHelper.show(Alert.AlertType.WARNING, "Transaction Closed", "This rental record is already closed.");
            return;
        }

        try {
            RentalRecord record = rentalService.returnVehicle(selected.getRentalId());

            String vName = vehicleRepo.findById(record.getVehicleId())
                    .map(v -> v.getBrand() + " " + v.getModel())
                    .orElse(record.getVehicleId());

            long targetDuration = ChronoUnit.DAYS.between(record.getRentalDate(), record.getExpectedReturnDate());
            long actualDuration = ChronoUnit.DAYS.between(record.getRentalDate(), record.getActualReturnDate());
            long lateDays = Math.max(0, actualDuration - targetDuration);

            String invoice = String.format(
                    "--- RENTAL INVOICE ---\n" +
                    "Rental ID:      %s\n" +
                    "Vehicle:        %s\n" +
                    "Rented Date:    %s\n" +
                    "Expected Date:  %s\n" +
                    "Returned Date:  %s\n" +
                    "Actual Days:    %d day(s)\n" +
                    "Late Days:      %d day(s)\n" +
                    "------------------------\n" +
                    "Total Cost Paid: $%.2f",
                    record.getRentalId(), vName, record.getRentalDate(),
                    record.getExpectedReturnDate(), record.getActualReturnDate(),
                    actualDuration, lateDays, record.getTotalCost()
            );

            AlertHelper.show(Alert.AlertType.INFORMATION, "Return Invoice Summary", invoice);
            refreshAllTables();
        } catch (RentalException e) {
            AlertHelper.show(Alert.AlertType.ERROR, "Return Failed", e.getMessage());
        }
    }

    
    @FXML
    private void handleTriggerAlerts(ActionEvent event) {
        notificationService.clearNotifications();
        rentalService.generateExpiryReminders();

        List<String> logs = notificationService.getSentNotifications();
        if (logs.isEmpty()) {
            AlertHelper.show(Alert.AlertType.INFORMATION, "Alert Trigger", "No rentals require warnings today.");
        } else {
            StringBuilder sb = new StringBuilder("Notifications Dispatched successfully:\n\n");
            for (String logMsg : logs) {
                sb.append(logMsg).append("\n");
                appendLog("Alert: " + logMsg);
            }
            AlertHelper.show(Alert.AlertType.WARNING, "Alerts Sent", sb.toString());
        }
    }

    
    @FXML
    private void handleClearLog(ActionEvent event) {
        logsArea.clear();
        java.io.File file = new java.io.File(getActivityLogFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    
    @FXML
    private void handleLogout(ActionEvent event) {
        rentalService.removeObserver(this);
        authService.logout();
        mainApp.showLoginScreen();
    }

    private void appendLog(String text) {
        String logLine = "[" + LocalDate.now() + "] " + text;
        logsArea.appendText(logLine + "\n");
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(getActivityLogFilePath(), true))) {
            writer.write(logLine);
            writer.newLine();
        } catch (java.io.IOException e) {
            System.err.println("Error writing activity log: " + e.getMessage());
        }
    }

    private String getActivityLogFilePath() {
        String userDir = System.getProperty("user.dir");
        java.io.File projectDir = new java.io.File(userDir);
        String dataDir = projectDir.getName().equals("vehicle-rental-project") ? "data" : "vehicle-rental-project/data";
        java.io.File dir = new java.io.File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dataDir + "/system_activity.txt";
    }

    private void loadActivityLog() {
        java.io.File file = new java.io.File(getActivityLogFilePath());
        if (!file.exists()) {
            return;
        }
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logsArea.appendText(line + "\n");
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading activity log: " + e.getMessage());
        }
    }




    @Override
    public void onRentalCreated(RentalRecord record, Customer customer, Vehicle vehicle) {
        Platform.runLater(() -> {
            appendLog("Transaction Created: " + record.getRentalId() + 
                    " | Vehicle: " + vehicle.getVehicleId() + " -> Customer: " + customer.getCustomerId());
            refreshAllTables();
        });
    }

    @Override
    public void onRentalReturned(RentalRecord record, Customer customer, Vehicle vehicle) {
        Platform.runLater(() -> {
            appendLog("Transaction Returned: " + record.getRentalId() + 
                    " | Vehicle: " + vehicle.getVehicleId() + " -> Paid Cost: $" + record.getTotalCost());
            refreshAllTables();
        });
    }

    @Override
    public void onExpiryReminderSent(RentalRecord record, Customer customer, String message) {
        Platform.runLater(() -> {
            appendLog("Expiry Warning Dispatched: " + message);
        });
    }
}
