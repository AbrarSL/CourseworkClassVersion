package com.github.abrarsl.courseworkclassversion;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final int burgersRequired;
    public static final String OBJECT_START_MARK = "CUSTOMERSTART";
    public static final String OBJECT_END_MARK = "CUSTOMEREND";

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

    public String stringifyState() {
        String state = OBJECT_START_MARK;
        state += String.format("%n%s%n%s%n%d%n", this.getFirstName(), this.getLastName(), this.getBurgersRequired());
        state += OBJECT_END_MARK;

        return state;
    }
}
