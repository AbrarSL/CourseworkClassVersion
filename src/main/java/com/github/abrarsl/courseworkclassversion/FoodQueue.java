package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;

public class FoodQueue {
    private final Customer[] queue;
    private static final int ITEM_PRICE = 650;
    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = 50;
    private static final int STOCK_WARN_THRESHOLD = 10;
    private int queueIncome;
    private int endIndex;
    private static int itemStock = 0;

    public FoodQueue(int queueLength) {
        this.queue = new Customer[queueLength];
        this.queueIncome = 0;
        this.endIndex = 0;
    }

    public FoodQueue(Customer[] queue, int income) {
        this.queue = queue;
        this.queueIncome = income;
        this.endIndex = this.queue.length;

        for (int i = 0; i < this.queue.length; i++) {
            if (this.queue[i] == null) {
                this.endIndex = i;
                break;
            }
        }

        System.out.println(this.endIndex);
    }

    public int getQueueLength() {
        return this.queue.length;
    }

    public int getQueueIncome() {
        return this.queueIncome;
    }

    public static int getItemStock() {
        return itemStock;
    }

    public static boolean isStockLow() {
        return getItemStock() <= STOCK_WARN_THRESHOLD;
    }

    public boolean isQueueFull() {
        return this.queue[this.getQueueLength() - 1] != null;
    }

    public static void setItemStock(int newItemStock) throws StockOutOfRangeException {
        if (newItemStock > MAX_STOCK || newItemStock < MIN_STOCK) {
            throw new StockOutOfRangeException(
                    String.format(
                            "Stock range is %d to %d.",
                            MIN_STOCK,
                            MAX_STOCK
                    )
            );
        }

        FoodQueue.itemStock = newItemStock;
    }

    private void validateSelection(int selection) throws SelectionOutOfRangeException {
        if (selection < 0 || selection >= this.getQueueLength()) {
            throw new SelectionOutOfRangeException(
                    String.format(
                            "Range is %d to %d.",
                            0,
                            this.getQueueLength() - 1
                    )
            );
        }
    }

    private void shiftLeftQueue(int startPosition) {
        for (int i = startPosition; i < this.getQueueLength() - 1; i++) {
            this.queue[i] = this.queue[i + 1];
        }

        this.queue[this.getQueueLength() - 1] = null;
        this.endIndex--;
    }

    public Customer getCustomer(int customerIndex) throws SelectionOutOfRangeException {
        this.validateSelection(customerIndex);
        return this.queue[customerIndex];
    }

    public void addCustomer(Customer customer) throws FullQueueException {
        if (this.endIndex >= this.queue.length) {
            throw new FullQueueException();
        }

        this.queue[this.endIndex++] = customer;
    }

    public Customer removeCustomer(int customerIndex) throws SelectionOutOfRangeException, CustomerNotFoundException {
        this.validateSelection(customerIndex);
        Customer customer = this.queue[customerIndex];

        if (customer == null) {
            throw new CustomerNotFoundException("No customer found in that position!");
        }

        this.shiftLeftQueue(customerIndex);
        return customer;
    }

    public Customer serveCustomer() throws CustomerNotFoundException, InsufficientStockException {
        final int queuePosition = 0;
        Customer customer = this.queue[queuePosition];

        if (customer == null) {
            throw new CustomerNotFoundException("No customer found in that position!");
        }

        int newFoodStock = itemStock - customer.getBurgersRequired();

        if (newFoodStock < MIN_STOCK) {
            throw new InsufficientStockException(String.valueOf(customer.getBurgersRequired()));
        }

        itemStock = newFoodStock;
        this.queueIncome += ITEM_PRICE * customer.getBurgersRequired();
        this.shiftLeftQueue(queuePosition);

        return customer;
    }

    public Customer[] searchCustomer(String searchTerm) {
        Customer[] tempQueue = new Customer[this.getQueueLength()];

        for (int i = 0; i < tempQueue.length; i++) {
            if (this.queue[i] != null && this.queue[i].getFullName().toUpperCase().contains(searchTerm.toUpperCase())) {
                tempQueue[i] = this.queue[i];
            }
        }

        return tempQueue;
    }

    @Override
    public String toString() {
        StringBuilder state = new StringBuilder(
                String.format(
                        "%d%n%d%n",
                        this.getQueueLength(),
                        this.getQueueIncome()
                )
        );

        for (Customer customer : this.queue) {
            if (customer != null) {
                state.append(customer);
            } else {
                state.append(String.format("null%n"));
            }
        }

        return state.toString();
    }
}
