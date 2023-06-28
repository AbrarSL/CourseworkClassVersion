package com.github.abrarsl;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final int burgersRequired;

    public Customer(String firstName, String lastName, int burgersRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public int getBurgersRequired() {
        return this.burgersRequired;
    }
}
