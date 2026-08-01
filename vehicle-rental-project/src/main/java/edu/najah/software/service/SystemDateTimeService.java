package edu.najah.software.service;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class SystemDateTimeService implements DateTimeService {

    
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    
    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
