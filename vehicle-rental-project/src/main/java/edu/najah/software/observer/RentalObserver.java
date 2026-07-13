package edu.najah.software.observer;

import edu.najah.software.model.RentalRecord;
import edu.najah.software.model.Customer;
import edu.najah.software.model.Vehicle;


public interface RentalObserver {

    void onRentalCreated(RentalRecord record, Customer customer, Vehicle vehicle);

    void onRentalReturned(RentalRecord record, Customer customer, Vehicle vehicle);

    void onExpiryReminderSent(RentalRecord record, Customer customer, String message);
}
