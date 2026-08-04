package edu.najah.software.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SystemDateTimeServiceTest {

    @Test
    public void testGetCurrentDateAndDateTime() {
        SystemDateTimeService service = new SystemDateTimeService();
        
        LocalDate date = service.getCurrentDate();
        assertNotNull(date);
        assertEquals(LocalDate.now(), date);

        LocalDateTime dateTime = service.getCurrentDateTime();
        assertNotNull(dateTime);
        assertTrue(dateTime.isBefore(LocalDateTime.now().plusSeconds(5)));
        assertTrue(dateTime.isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}
