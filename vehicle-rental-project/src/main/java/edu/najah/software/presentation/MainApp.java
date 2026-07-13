package edu.najah.software.presentation;

import edu.najah.software.model.*;
import edu.najah.software.repository.*;
import edu.najah.software.service.*;
import edu.najah.software.strategy.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class MainApp extends Application {

    private AuthenticationService authService;
    private CatalogService catalogService;
    private RentalService rentalService;
    private EmailNotificationService notificationService;

    private UserRepository userRepo;
    private VehicleRepository vehicleRepo;
    private CustomerRepository customerRepo;
    private RentalRepository rentalRepo;

    private Stage primaryStage;

    private String getDbPath() {
        if (new java.io.File("vehicle-rental-project").exists()) {
            return "vehicle-rental-project/data";
        }
        return "data";
    }

    
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        String dbPath = getDbPath();
        
        this.userRepo = new FileUserRepository(dbPath);
        this.vehicleRepo = new FileVehicleRepository(dbPath);
        this.customerRepo = new FileCustomerRepository(dbPath);
        this.rentalRepo = new FileRentalRepository(dbPath);

        
        this.authService = new AuthenticationService(userRepo);
        this.catalogService = new CatalogService(vehicleRepo, authService);
        this.notificationService = new EmailNotificationService();

        DateTimeService dateTimeService = new SystemDateTimeService();
        RentalPricingStrategy pricingStrategy = new DefaultRentalPricingStrategy();

        List<RentalValidationStrategy> validationStrategies = new ArrayList<>();
        validationStrategies.add(new DurationValidationStrategy());
        validationStrategies.add(new MotorcycleAgeValidationStrategy());
        validationStrategies.add(new TruckLicenseValidationStrategy());
        validationStrategies.add(new ElectricVehicleBatteryValidationStrategy());

        this.rentalService = new RentalService(
                rentalRepo, vehicleRepo, customerRepo, dateTimeService, pricingStrategy, validationStrategies
        );

        
        this.rentalService.addObserver(this.notificationService);

        
        showLoginScreen();
    }

    
    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.init(this, authService, catalogService, rentalService, notificationService, userRepo, vehicleRepo, customerRepo, rentalRepo);

            primaryStage.setTitle("Vehicle Rental Management System - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void showManagerScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager.fxml"));
            Parent root = loader.load();

            ManagerController controller = loader.getController();
            controller.init(this, authService, catalogService, rentalService, notificationService, userRepo, vehicleRepo, customerRepo, rentalRepo);

            
            rentalService.addObserver(controller);

            primaryStage.setTitle("Manager Dashboard");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void showCustomerScreen(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customer.fxml"));
            Parent root = loader.load();

            CustomerController controller = loader.getController();
            controller.init(this, customer, authService, catalogService, rentalService, notificationService, userRepo, vehicleRepo, customerRepo, rentalRepo);

            
            rentalService.addObserver(controller);

            primaryStage.setTitle("Customer Portal");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
