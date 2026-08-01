package edu.najah.software.repository;

import edu.najah.software.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {

    private Path tempDir;
    private String tempDirPath;

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("rent-test-data");
        tempDirPath = tempDir.toAbsolutePath().toString();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);
    }

    @Test
    public void testFileCustomerRepository() {
        FileCustomerRepository repo = new FileCustomerRepository(tempDirPath);
        
        List<Customer> customers = repo.findAll();
        assertEquals(4, customers.size());
        
        Customer newCust = new Customer("C005", "Test User", "test@test.com", 22, "REGULAR", "pass");
        repo.save(newCust);
        
        Optional<Customer> found = repo.findById("C005");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertEquals("pass", found.get().getPassword());

        Optional<Customer> notFound = repo.findById("NON_EXISTENT");
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFileUserRepository() {
        FileUserRepository repo = new FileUserRepository(tempDirPath);
        
        List<Manager> managers = repo.findAll();
        assertEquals(1, managers.size());
        assertEquals("admin", managers.get(0).getUsername());
        
        Manager m = new Manager("manager1", "pass1");
        repo.save(m);
        
        Optional<Manager> found = repo.findByUsername("manager1");
        assertTrue(found.isPresent());
        assertEquals("pass1", found.get().getPassword());
        
        Optional<Manager> notFound = repo.findByUsername("NON_EXISTENT");
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFileRentalRepository() {
        FileRentalRepository repo = new FileRentalRepository(tempDirPath);
        
        assertTrue(repo.findAll().isEmpty());
        
        RentalRecord record = new RentalRecord("R1", "C001", "V001", LocalDate.now(), LocalDate.now().plusDays(5));
        repo.save(record);
        
        List<RentalRecord> records = repo.findAll();
        assertEquals(1, records.size());
        assertEquals("C001", records.get(0).getCustomerId());
        assertNull(records.get(0).getActualReturnDate());
        assertFalse(records.get(0).isClosed());

        record.setActualReturnDate(LocalDate.now().plusDays(3));
        record.setTotalCost(150.0);
        record.setClosed(true);
        repo.save(record);

        RentalRecord updated = repo.findById("R1").orElse(null);
        assertNotNull(updated);
        assertEquals(LocalDate.now().plusDays(3), updated.getActualReturnDate());
        assertEquals(150.0, updated.getTotalCost());
        assertTrue(updated.isClosed());
    }

    @Test
    public void testFileVehicleRepository() {
        FileVehicleRepository repo = new FileVehicleRepository(tempDirPath);
        
        List<Vehicle> vehicles = repo.findAll();
        assertEquals(5, vehicles.size());
        
        Car car = new Car("V100", "BrandA", "ModelA", "PLATE-A", 10.0, true);
        Motorcycle bike = new Motorcycle("V101", "BrandB", "ModelB", "PLATE-B", 20.0, true);
        Van van = new Van("V102", "BrandC", "ModelC", "PLATE-C", 30.0, true);
        Truck truck = new Truck("V103", "BrandD", "ModelD", "PLATE-D", 40.0, true);
        ElectricVehicle ev = new ElectricVehicle("V104", "BrandE", "ModelE", "PLATE-E", 50.0, true, 90.0);
        
        repo.saveAll(Arrays.asList(car, bike, van, truck, ev));
        
        assertEquals(10, repo.findAll().size());
        
        Vehicle parsedCar = repo.findById("V100").orElse(null);
        assertNotNull(parsedCar);
        assertEquals("Car", parsedCar.getVehicleType());
        
        Vehicle parsedBike = repo.findById("V101").orElse(null);
        assertNotNull(parsedBike);
        assertEquals("Motorcycle", parsedBike.getVehicleType());

        Vehicle parsedVan = repo.findById("V102").orElse(null);
        assertNotNull(parsedVan);
        assertEquals("Van", parsedVan.getVehicleType());

        Vehicle parsedTruck = repo.findById("V103").orElse(null);
        assertNotNull(parsedTruck);
        assertEquals("Truck", parsedTruck.getVehicleType());

        Vehicle parsedEv = repo.findById("V104").orElse(null);
        assertNotNull(parsedEv);
        assertEquals("ElectricVehicle", parsedEv.getVehicleType());
        assertEquals(90.0, ((ElectricVehicle) parsedEv).getBatteryPercentage());
    }
}
