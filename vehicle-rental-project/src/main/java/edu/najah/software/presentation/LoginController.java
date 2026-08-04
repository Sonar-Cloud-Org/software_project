package edu.najah.software.presentation;

import edu.najah.software.model.Customer;
import edu.najah.software.repository.*;
import edu.najah.software.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.function.UnaryOperator;


public class LoginController {

    private MainApp mainApp;
    private AuthenticationService authService;
    private CustomerRepository customerRepo;

    @FXML
    private TextField idOrUserField;

    @FXML
    private PasswordField passField;

    
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
        this.customerRepo = customerRepo;
    }

    
    @FXML
    private void handleLogin(ActionEvent event) {
        String idOrUser = idOrUserField.getText().trim();
        String pass = passField.getText().trim();

        if (idOrUser.isEmpty()) {
            AlertHelper.show(Alert.AlertType.ERROR, "Error", "Please enter your Username or Customer ID.");
            return;
        }

        
        Optional<Customer> customerOpt = customerRepo.findAll().stream()
                .filter(c -> c.getCustomerId().equalsIgnoreCase(idOrUser) || c.getName().equalsIgnoreCase(idOrUser))
                .findFirst();

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword().equals(pass)) {
                idOrUserField.setText("");
                passField.setText("");
                mainApp.showCustomerScreen(customer);
            } else {
                AlertHelper.show(Alert.AlertType.ERROR, "Login Failed", "Invalid password for Customer: " + idOrUser);
            }
        } else {
            
            if (authService.login(idOrUser, pass)) {
                idOrUserField.setText("");
                passField.setText("");
                mainApp.showManagerScreen();
            } else {
                AlertHelper.show(Alert.AlertType.ERROR, "Login Failed",
                        "ID, Username, or Password invalid.");
            }
        }
    }

    
    @FXML
    private void handleRegister(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New Customer");
        dialog.setHeaderText("Please fill out your profile details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameF = new TextField();
        nameF.setPromptText("Full Name");
        TextField emailF = new TextField();
        emailF.setPromptText("Email Address");
        TextField ageF = new TextField();
        ageF.setPromptText("Age");
        ComboBox<String> licCombo = new ComboBox<>();
        licCombo.getItems().addAll("REGULAR", "HEAVY");
        licCombo.setValue("REGULAR");
        PasswordField passF = new PasswordField();
        passF.setPromptText("Account Password");

        
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) {
                return change;
            }
            return null;
        };
        ageF.setTextFormatter(new TextFormatter<>(digitFilter));

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameF, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailF, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageF, 1, 2);
        grid.add(new Label("License Type:"), 0, 3);
        grid.add(licCombo, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passF, 1, 4);

        dialogPane.setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String ageStr = ageF.getText().trim();
            String license = licCombo.getValue();
            String password = passF.getText().trim();

            if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty() || password.isEmpty()) {
                AlertHelper.show(Alert.AlertType.ERROR, "Registration Error", "All fields are required.");
                return;
            }

            
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!email.matches(emailRegex)) {
                AlertHelper.show(Alert.AlertType.ERROR, "Registration Error", "Please enter a valid email address (e.g. name@domain.com).");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                AlertHelper.show(Alert.AlertType.ERROR, "Registration Error", "Age must be a valid number.");
                return;
            }

            
            String nextId = "C00" + (customerRepo.findAll().size() + 1);
            Customer newCustomer = new Customer(nextId, name, email, age, license, password);
            customerRepo.save(newCustomer);

            AlertHelper.show(Alert.AlertType.INFORMATION, "Success",
                    "Registration Successful!\nYour Login Customer ID is: " + nextId + "\nPassword: " + password);
        }
    }
}
