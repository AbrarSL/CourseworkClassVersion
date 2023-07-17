package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;

/**
 * A class that is used to store and manipulate a queue of Customer objects.
 * The class will also statically store info such as stock information.
 * Each instance of the class will also track the total income.
 */
public class FoodQueue {
    private final Customer[] queue;
    private static final int ITEM_PRICE = 650;
    private static final int MIN_STOCK = 0;
    public static final int MAX_STOCK = 50;
    private static final int STOCK_WARN_THRESHOLD = 10;
    public static final String FOODQUEUE_START_MARK = "FOODQUEUE_START";
    private int queueIncome;
    private int endIndex;
    private static int itemStock = 0;

    /**
     * @param queueLength The maximum length of the {@link FoodQueue} to construct.
     */
    public FoodQueue(int queueLength) {
        this.queue = new Customer[queueLength];
        this.queueIncome = 0;
        this.endIndex = 0;
    }

    /**
     * @param queue The internal array. Ensure there are no holes in this array.
     * @param income The queue income value.
     */
    public FoodQueue(Customer[] queue, int income) {
        this.queue = queue;
        this.queueIncome = income;
        this.endIndex = this.queue.length;

        for (int i = 0; i < this.queue.length; i++) { // Linear search for the end of the queue.
            if (this.queue[i] == null) {
                this.endIndex = i;
                break;
            }
        }

        System.out.println(this.endIndex);
    }

    /**
     * @return The length of the particular {@link FoodQueue}.
     */
    public int getQueueLength() {
        return this.queue.length;
    }

    /**
     * @return The total income for the particular {@link FoodQueue}.
     */
    public int getQueueIncome() {
        return this.queueIncome;
    }

    /**
     * @return The stock information for all {@link FoodQueue}.
     */
    public static int getItemStock() {
        return itemStock;
    }

    /**
     * Will set {@link FoodQueue#itemStock}.
     * @param newItemStock The stock value to be set.
     * @throws StockOutOfRangeException Will be thrown if given stock is not between
     * {@link FoodQueue#MIN_STOCK} and {@link FoodQueue#MAX_STOCK}
     */
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

    /**
     * @return 'true' if {@link FoodQueue#itemStock} is lower than or equal to {@link FoodQueue#STOCK_WARN_THRESHOLD}.
     */
    public static boolean isStockLow() {
        return getItemStock() <= STOCK_WARN_THRESHOLD;
    }

    /**
     * @return 'true' if {@link FoodQueue} is full.
     */
    public boolean isQueueFull() {
        return this.endIndex >= this.getQueueLength();
    }

    /**
     * @param selection The number to be checked.
     * @throws SelectionOutOfRangeException If the given number is out of bounds for the internal queue.
     */
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

    /**
     * Shifts all elements to the right of the given point one step to the left.
     * @param startPosition The index to start shifting from.
     */
    private void shiftLeftQueue(int startPosition) {
        for (int i = startPosition; i < this.getQueueLength() - 1; i++) {
            this.queue[i] = this.queue[i + 1];
        }

        this.queue[this.getQueueLength() - 1] = null;
        this.endIndex--;
    }

    /**
     * @param customerIndex The position of the {@link Customer} in the {@link FoodQueue}.
     * @return Reference to the {@link Customer}.
     * @throws SelectionOutOfRangeException
     */
    public Customer getCustomer(int customerIndex) throws SelectionOutOfRangeException {
        this.validateSelection(customerIndex);
        return this.queue[customerIndex];
    }

    /**
     * Adds a {@link Customer} to the end of the {@link FoodQueue}.
     * @param customer The {@link Customer} to be added.
     * @throws FullQueueException If {@link FoodQueue} is full.
     */
    public void addCustomer(Customer customer) throws FullQueueException {
        if (this.isQueueFull()) {
            throw new FullQueueException();
        }

        this.queue[this.endIndex++] = customer;
    }

    /**
     * Remove {@link Customer} from the given position. All elements are shifted to remove holes in the {@link FoodQueue}.
     * @param customerIndex The index from which to remove the {@link Customer}.
     * @return A reference to the {@link Customer} that was removed.
     * @throws SelectionOutOfRangeException If given index is out of range.
     * @throws CustomerNotFoundException If {@link Customer} cannot be found at the given position.
     */
    public Customer removeCustomer(int customerIndex) throws SelectionOutOfRangeException, CustomerNotFoundException {
        this.validateSelection(customerIndex);
        Customer customer = this.queue[customerIndex];

        if (customer == null) {
            throw new CustomerNotFoundException("No customer found in that position!");
        }

        this.shiftLeftQueue(customerIndex);
        return customer;
    }

    /**
     * Serve the {@link Customer} at the front of the {@link FoodQueue}.
     * @return A reference to the served {@link Customer}.
     * @throws CustomerNotFoundException If a {@link Customer} is not available at the front of the {@link FoodQueue}.
     * @throws InsufficientStockException If the stock level is not enough to serve the {@link Customer}.
     */
    public Customer serveCustomer() throws CustomerNotFoundException, InsufficientStockException {
        final int queuePosition = 0;
        Customer customer = this.queue[queuePosition];

        if (customer == null) {
            throw new CustomerNotFoundException("No customer found in that position!");
        }

        int newFoodStock = itemStock - customer.getBurgersRequired();

        if (newFoodStock < MIN_STOCK) { // Ensure there is enough stock to serve customer
            throw new InsufficientStockException(String.valueOf(customer.getBurgersRequired()));
        }

        itemStock = newFoodStock;
        this.queueIncome += ITEM_PRICE * customer.getBurgersRequired(); // Update queue income
        this.shiftLeftQueue(queuePosition);

        return customer;
    }

    /**
     * Search for {@link Customer}s with the given search term within their name.
     * Searches are case-insensitive.
     * @param searchTerm The search term that is checked.
     * @return An array of {@link Customer}s who match the criteria.
     */
    public Customer[] searchCustomer(String searchTerm) {
        Customer[] tempQueue = new Customer[this.getQueueLength()];

        for (int i = 0; i < tempQueue.length; i++) {
            if (this.queue[i] != null && this.queue[i].getFullName().toUpperCase().contains(searchTerm.toUpperCase())) {
                tempQueue[i] = this.queue[i];
            }
        }

        return tempQueue;
    }

    /**
     * @return A string representation of the {@link FoodQueue} instance.
     */
    @Override
    public String toString() {
        StringBuilder state = new StringBuilder(
                String.format(
                        "%s%n%d%n%d%n",
                        FOODQUEUE_START_MARK,
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
