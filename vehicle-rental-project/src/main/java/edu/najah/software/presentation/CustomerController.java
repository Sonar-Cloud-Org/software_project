package edu.najah.software.presentation;

import edu.najah.software.model.*;
import edu.najah.software.repository.*;
import edu.najah.software.service.*;
import edu.najah.software.observer.RentalObserver;
import edu.najah.software.exception.RentalException;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.function.UnaryOperator;


public class CustomerController implements RentalObserver {

    private MainApp mainApp;
    private Customer activeCustomer;

    private AuthenticationService authService;
    private CatalogService catalogService;
    private RentalService rentalService;
    private VehicleRepository vehicleRepo;
    private RentalRepository rentalRepo;

    @FXML
    private Label customerWelcomeLabel;

    @FXML
    private TableView<Vehicle> fleetTable;

    @FXML
    private TableColumn<Vehicle, String> colFleetId;
    @FXML
    private TableColumn<Vehicle, String> colFleetType;
    @FXML
    private TableColumn<Vehicle, String> colFleetBrand;
    @FXML
    private TableColumn<Vehicle, String> colFleetModel;
    @FXML
    private TableColumn<Vehicle, String> colFleetPlate;
    @FXML
    private TableColumn<Vehicle, Double> colFleetRate;
    @FXML
    private TableColumn<Vehicle, String> colFleetExtra;

    @FXML
    private ComboBox<String> selectedVehicleCombo;

    @FXML
    private TextField durationField;

    @FXML
    private TableView<RentalRecord> myRentalsTable;

    @FXML
    private TableColumn<RentalRecord, String> colMyRentId;
    @FXML
    private TableColumn<RentalRecord, String> colMyVehId;
    @FXML
    private TableColumn<RentalRecord, String> colMyVehName;
    @FXML
    private TableColumn<RentalRecord, String> colMyRentStart;
    @FXML
    private TableColumn<RentalRecord, String> colMyRentDue;


    public void init(MainApp mainApp,
                     Customer customer,
                     AuthenticationService authService,
                     CatalogService catalogService,
                     RentalService rentalService,
                     EmailNotificationService notificationService,
                     UserRepository userRepo,
                     VehicleRepository vehicleRepo,
                     CustomerRepository customerRepo,
                     RentalRepository rentalRepo) {
        this.mainApp = mainApp;
        this.activeCustomer = customer;
        this.authService = authService;
        this.catalogService = catalogService;
        this.rentalService = rentalService;
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;

        customerWelcomeLabel.setText("Welcome Customer: " + activeCustomer.getName() +
                " | Age: " + activeCustomer.getAge() + " | License: " + activeCustomer.getLicenseType());


        colFleetId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleId()));
        colFleetType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleType()));
        colFleetBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        colFleetModel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModel()));
        colFleetPlate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLicensePlate()));
        colFleetRate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDailyRate()));
        colFleetExtra.setCellValueFactory(data -> {
            Vehicle v = data.getValue();
            if (v instanceof ElectricVehicle) {
                return new SimpleStringProperty(((ElectricVehicle) v).getBatteryPercentage() + "% Battery");
            }
            return new SimpleStringProperty("-");
        });

        colMyRentId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRentalId()));
        colMyVehId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicleId()));
        colMyVehName.setCellValueFactory(data -> {
            String vId = data.getValue().getVehicleId();
            String name = vehicleRepo.findById(vId).map(v -> v.getBrand() + " " + v.getModel()).orElse("-");
            return new SimpleStringProperty(name);
        });
        colMyRentStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRentalDate().toString()));
        colMyRentDue.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpectedReturnDate().toString()));

        
        fleetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVehicleCombo.setValue(newSelection.getVehicleId());
            }
        });

        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) {
                return change;
            }
            return null;
        };
        durationField.setTextFormatter(new TextFormatter<>(digitFilter));

        refreshCustomerUI();
    }


    private void refreshCustomerUI() {
        var availableFleet = vehicleRepo.findAll().stream()
                .filter(Vehicle::isAvailable)
                .collect(Collectors.toList());

        fleetTable.setItems(FXCollections.observableArrayList(availableFleet));

        selectedVehicleCombo.getItems().clear();
        for (Vehicle v : availableFleet) {
            selectedVehicleCombo.getItems().add(v.getVehicleId());
        }

        var activeRentals = rentalRepo.findAll().stream()
                .filter(r -> r.getCustomerId().equalsIgnoreCase(activeCustomer.getCustomerId()) && !r.isClosed())
                .collect(Collectors.toList());

        myRentalsTable.setItems(FXCollections.observableArrayList(activeRentals));
    }

    
    @FXML
    private void handleRentVehicle(ActionEvent event) {
        String vid = selectedVehicleCombo.getValue();
        String durationStr = durationField.getText().trim();

        if (vid == null || vid.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Select Vehicle", "Please select a vehicle to book.");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Duration", "Please enter a valid numeric number of days.");
            return;
        }

        try {
            RentalRecord record = rentalService.rentVehicle(activeCustomer.getCustomerId(), vid, duration);
            showAlert(Alert.AlertType.INFORMATION, "Booking Success", 
                    "Vehicle rented successfully!\nTransaction ID: " + record.getRentalId() +
                    "\nExpected return date: " + record.getExpectedReturnDate());

            durationField.setText("");
        } catch (RentalException e) {
            showAlert(Alert.AlertType.WARNING, "Booking Rejected", "Rental Rejected: " + e.getMessage());
        }
    }

    
    @FXML
    private void handleLogout(ActionEvent event) {
        rentalService.removeObserver(this);
        activeCustomer = null;
        mainApp.showLoginScreen();
    }

    
    
    
    @Override
    public void onRentalCreated(RentalRecord record, Customer customer, Vehicle vehicle) {
        Platform.runLater(this::refreshCustomerUI);
    }

    @Override
    public void onRentalReturned(RentalRecord record, Customer customer, Vehicle vehicle) {
        Platform.runLater(this::refreshCustomerUI);
    }

    @Override
    public void onExpiryReminderSent(RentalRecord record, Customer customer, String message) {
        
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
