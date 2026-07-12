package edu.najah.software.model;

public class Customer {

    private final String customerId;

    private final String name;

    private final String email;

    private final int age;

    private final String licenseType;

    private final String password;

    public Customer(String customerId, String name, String email, int age, String licenseType, String password) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.licenseType = licenseType;
        this.password = password;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public String getPassword() {
        return password;
    }
}
