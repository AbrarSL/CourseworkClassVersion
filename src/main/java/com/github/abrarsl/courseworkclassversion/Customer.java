package com.github.abrarsl.courseworkclassversion;

/**
 * A class that is used to represent a customer.
 * The customer's first and last names as well as their order is stored.
 */
public class Customer {
    private final String firstName;
    private final String lastName;
    private final int burgersRequired;
    public static final String INFO_DELIMITER = ",";

    /**
     * @param firstName Customer's first name.
     * @param lastName Customer's last name.
     * @param burgersRequired The amount of burgers required.
     */
    public Customer(String firstName, String lastName, int burgersRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    /**
     * @return The first name of the {@link Customer}.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @return The last name of the {@link Customer}.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @return The full name of the {@link Customer}.
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * @return The number of burgers required by the {@link Customer}.
     */
    public int getBurgersRequired() {
        return this.burgersRequired;
    }

    /**
     * @return A string representation of a {@link Customer} instance.
     */
    @Override
    public String toString() {
        return String.format(
                "%s%s%s%s%d%n",
                this.getFirstName(),
                INFO_DELIMITER,
                this.getLastName(),
                INFO_DELIMITER,
                this.getBurgersRequired()
        );
    }
}
