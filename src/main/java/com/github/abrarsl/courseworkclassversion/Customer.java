package com.github.abrarsl.courseworkclassversion;

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

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public int getBurgersRequired() {
        return this.burgersRequired;
    }

    @Override
    public String toString() {
        return String.format(
                "%s,%s,%d%n",
                this.getFirstName(),
                this.getLastName(),
                this.getBurgersRequired()
        );
    }
}
