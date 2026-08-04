package edu.najah.software.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleFactoryTest {

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor<VehicleFactory> constructor = VehicleFactory.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        
        VehicleFactory instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    public void testCreateCar() {
        Vehicle vehicle = VehicleFactory.create("Car", "V001", "Toyota", "Corolla", "ABC-123", 50.0, true, 0.0);
        assertNotNull(vehicle);
        assertTrue(vehicle instanceof Car);
        assertEquals("V001", vehicle.getVehicleId());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals("ABC-123", vehicle.getLicensePlate());
        assertEquals(50.0, vehicle.getDailyRate());
        assertTrue(vehicle.isAvailable());
    }

    @Test
    public void testCreateMotorcycle() {
        Vehicle vehicle = VehicleFactory.create("Motorcycle", "V002", "Yamaha", "R1", "XYZ-789", 40.0, true, 0.0);
        assertNotNull(vehicle);
        assertTrue(vehicle instanceof Motorcycle);
        assertEquals("V002", vehicle.getVehicleId());
        assertEquals(40.0, vehicle.getDailyRate());
    }

    @Test
    public void testCreateVan() {
        Vehicle vehicle = VehicleFactory.create("Van", "V003", "Ford", "Transit", "VAN-456", 75.0, true, 0.0);
        assertNotNull(vehicle);
        assertTrue(vehicle instanceof Van);
        assertEquals("V003", vehicle.getVehicleId());
        assertEquals(75.0, vehicle.getDailyRate());
    }

    @Test
    public void testCreateTruck() {
        Vehicle vehicle = VehicleFactory.create("Truck", "V004", "Volvo", "FH16", "TRK-789", 100.0, true, 0.0);
        assertNotNull(vehicle);
        assertTrue(vehicle instanceof Truck);
        assertEquals("V004", vehicle.getVehicleId());
        assertEquals(100.0, vehicle.getDailyRate());
    }

    @Test
    public void testCreateElectricVehicle() {
        Vehicle vehicle = VehicleFactory.create("ElectricVehicle", "V005", "Tesla", "Model 3", "EV-111", 80.0, true, 85.0);
        assertNotNull(vehicle);
        assertTrue(vehicle instanceof ElectricVehicle);
        assertEquals("V005", vehicle.getVehicleId());
        assertEquals(80.0, vehicle.getDailyRate());
        assertEquals(85.0, ((ElectricVehicle) vehicle).getBatteryPercentage());
    }

    @Test
    public void testCreateUnsupportedType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            VehicleFactory.create("Bicycle", "V006", "Giant", "Escape", "N/A", 15.0, true, 0.0);
        });
        assertTrue(exception.getMessage().contains("Unsupported vehicle type: Bicycle"));
    }
}
