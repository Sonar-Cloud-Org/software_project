package edu.najah.software.service;

import edu.najah.software.model.Customer;
import edu.najah.software.model.RentalRecord;
import edu.najah.software.model.Vehicle;
import edu.najah.software.model.Car;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmailNotificationServiceTest {

    private EmailNotificationService service;
    private Customer customer;
    private RentalRecord record;
    private Vehicle vehicle;

    @BeforeEach
    public void setUp() {
        service = new EmailNotificationService();
        service.clearNotifications();
        customer = new Customer("C100", "John Doe", "john@example.com", 25, "REGULAR", "pass123");
        record = new RentalRecord("R100", "C100", "V100", LocalDate.now(), LocalDate.now().plusDays(5));
        vehicle = new Car("V100", "Toyota", "Corolla", "ABC-123", 50.0, true);
    }

    @AfterEach
    public void tearDown() {
        service.clearNotifications();
    }

    @Test
    public void testOnExpiryReminderSentAndDeDuplication() {
        String msg = "Your rental is due today.";
        
        // First reminder
        service.onExpiryReminderSent(record, customer, msg);
        
        List<String> list = service.getSentNotifications();
        assertEquals(1, list.size());
        assertTrue(list.get(0).contains("john@example.com"));
        assertTrue(list.get(0).contains(msg));

        // Duplicate reminder on same day
        service.onExpiryReminderSent(record, customer, msg);
        
        // Size should still be 1 (de-duplicated)
        List<String> list2 = service.getSentNotifications();
        assertEquals(1, list2.size());
    }

    @Test
    public void testOnRentalCreatedAndReturnedNoOps() {
        // These are blank observer operations (no-ops), calling them should not throw or log anything
        assertDoesNotThrow(() -> service.onRentalCreated(record, customer, vehicle));
        assertDoesNotThrow(() -> service.onRentalReturned(record, customer, vehicle));
        assertTrue(service.getSentNotifications().isEmpty());
    }

    @Test
    public void testClearNotifications() {
        service.onExpiryReminderSent(record, customer, "Reminder 1");
        assertFalse(service.getSentNotifications().isEmpty());

        service.clearNotifications();
        assertTrue(service.getSentNotifications().isEmpty());
    }
}
