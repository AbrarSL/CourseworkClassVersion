package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;

public class FoodQueue {
    private final Customer[] queue;
    private static final int ITEM_PRICE = 650;
    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = 50;
    private static final int STOCK_WARN_THRESHOLD = 10;
    public static final String OBJECT_START_MARK = "FOODQUEUESTART";
    public static final String OBJECT_END_MARK = "FOODQUEUEEND";
    public static final String LENGTH_START_MARK = "LENGTHSTART";
    public static final String LENGTH_END_MARK = "LENGTHEND";
    public static final String INCOME_START_MARK = "INCOMESTART";
    public static final String INCOME_END_MARK = "INCOMEEND";
    public static final String CUSTOMER_ARRAY_START_MARK = "CUSTOMERARRAYSTART";
    public static final String CUSTOMER_ARRAY_END_MARK = "CUSTOMERARRAYEND";
    private int queueIncome;
    private static int itemStock = 0;

    public FoodQueue(int queueLength) {
        this.queue = new Customer[queueLength];
        this.queueIncome = 0;
    }

    public FoodQueue(Customer[] queue, int income) {
        this.queue = queue;
        this.queueIncome = income;
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

    public static void setItemStock(int newItemStock) throws StockOutOfRange {
        if (newItemStock > MAX_STOCK || newItemStock < MIN_STOCK) {
            throw new StockOutOfRange(String.format("Stock range is %d to %d.", MIN_STOCK, MAX_STOCK));
        }

        FoodQueue.itemStock = newItemStock;
    }

    private void validateSelection(int selection) throws SelectionOutOfRangeException {
        if (selection < 0 || selection >= this.getQueueLength()) {
            throw new SelectionOutOfRangeException(String.format("Range is %d to %d.", 0, this.getQueueLength() - 1));
        }
    }

    private void shiftLeftQueue(int startPosition) {
        for (int i = startPosition; i < this.getQueueLength() - 1; i++) {
            this.queue[i] = this.queue[i + 1];
        }

        this.queue[this.getQueueLength() - 1] = null;
    }

    public Customer getCustomer(int customerIndex) throws SelectionOutOfRangeException {
        this.validateSelection(customerIndex);
        return this.queue[customerIndex];
    }

    public void addCustomer(Customer customer) throws FullQueueException {
        if (this.queue[this.getQueueLength() - 1] != null) {
            throw new FullQueueException();
        }

        for (int i = 0; i < this.getQueueLength(); i++) {
            if (this.queue[i] == null) {
                this.queue[i] = customer;
                return;
            }
        }
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

    public String stringifyState() {
        String state = OBJECT_START_MARK;

        state += String.format("%n%s%n%d%n%s", LENGTH_START_MARK, this.getQueueLength(), LENGTH_END_MARK);
        state += String.format("%n%s%n%d%n%s", INCOME_START_MARK, this.getQueueLength(), INCOME_END_MARK);

        state += String.format("%n%s", CUSTOMER_ARRAY_START_MARK);

        for (Customer customer : this.queue) {
            if (customer == null) {
                break;
            }

            state += String.format("%n%s", customer.stringifyState());
        }

        state += String.format("%n%s%n", CUSTOMER_ARRAY_END_MARK);

        state += OBJECT_END_MARK;

        return state;
    }
}
