package com.github.abrarsl;

import java.util.ArrayList;

public class FoodQueue {
    private final ArrayList<Customer> queue;
    private final int queueLength;

    public FoodQueue(int queueLength) {
        this.queue = new ArrayList<>(queueLength);
        this.queueLength = queueLength;
    }

    public int getQueueLength() {
        return this.queueLength;
    }

    public Customer removeCustomer(int customerIndex) {
        if (customerIndex < 0 || customerIndex >= this.queueLength) {
            return null;
        }

        return this.queue.remove(customerIndex);
    }

    public boolean addCustomer(Customer customer) {
        if (this.queue.size() >= this.queueLength) {
            return false;
        }

        return this.queue.add(customer);
    }
}
