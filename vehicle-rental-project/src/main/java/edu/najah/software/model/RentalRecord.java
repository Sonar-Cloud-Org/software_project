package edu.najah.software.model;

import java.time.LocalDate;

public class RentalRecord {

    private final String rentalId;

    private final String customerId;

    private final String vehicleId;

    private final LocalDate rentalDate;

    private final LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private double totalCost;

    private boolean isClosed;

    public RentalRecord(String rentalId, String customerId, String vehicleId, LocalDate rentalDate, LocalDate expectedReturnDate) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = null;
        this.totalCost = 0.0;
        this.isClosed = false;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
