package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.CustomerNotFoundException;
import com.github.abrarsl.courseworkclassversion.exceptions.FullQueueException;

/**
 * A class that can be used as a waiting queue for customers.
 * This is implemented as a circular queue data structure.
 */
public class WaitingQueue {
    public static final String WAITINGQUEUE_START_MARK = "WAITINGQUEUE_START";
    private final Customer[] queue;
    private int frontIndex;
    private int rearIndex;

    /**
     * @param length The maximum length of the {@link WaitingQueue}.
     */
    public WaitingQueue(int length) {
        this.queue = new Customer[length];

        // Indicate empty queue with index trackers
        this.frontIndex = -1;
        this.rearIndex = -1;
    }

    /**
     * @param queue An array that will be used to construct a {@link WaitingQueue}. Ensure that the array has no holes.
     */
    public WaitingQueue(Customer[] queue) {
        this.queue = queue;

        if (this.queue[0] == null) { // Set index trackers if empty
            this.frontIndex = -1;
            this.rearIndex = -1;
        } else {
            this.frontIndex = 0;
            this.rearIndex = this.queue.length - 1;

            for (int i = 0; i < this.queue.length; i++) { // Search for the last valid customer
                if (this.queue[i] == null) {
                    this.rearIndex = i - 1;
                }
            }
        }
    }

    /**
     * Adds {@link Customer} to the rear of the {@link WaitingQueue}.
     * @param customer The customer to be added onto the {@link WaitingQueue} instance.
     * @throws FullQueueException If the {@link WaitingQueue} instance is full.
     */
    public void enqueue(Customer customer) throws FullQueueException {
        if (this.isQueueFull()) {
            throw new FullQueueException();
        }

        if (this.isQueueEmpty()) { // Set frontIndex if queue was previously empty
            this.frontIndex = 0;
        }

        this.rearIndex = (this.rearIndex + 1) % this.queue.length; // Increment with overflow
        this.queue[this.rearIndex] = customer;
    }

    /**
     * Remove {@link Customer} from the front of the {@link WaitingQueue}.
     * @return The {@link Customer} that was removed.
     * @throws CustomerNotFoundException If {@link Customer} cannot be found (queue is empty).
     */
    public Customer dequeue() throws CustomerNotFoundException {
        if (this.isQueueEmpty()) {
            throw new CustomerNotFoundException();
        }

        // Remove customer from queue
        Customer customer = this.queue[this.frontIndex];
        this.queue[this.frontIndex] = null;

        if (this.frontIndex == this.rearIndex) { // If removed element is last element, indicate queue is empty
            this.frontIndex = -1;
            this.rearIndex = -1;
        } else {
            this.frontIndex = (this.frontIndex + 1) % this.queue.length; // Increment with overflow
        }

        return customer;
    }

    /**
     * @return 'true' if {@link WaitingQueue} is empty.
     */
    public boolean isQueueEmpty() {
        return this.frontIndex == -1;
    }

    /**
     * @return 'true' if {@link WaitingQueue} is full.
     */
    private boolean isQueueFull() {
        // Check if incrementing rear index will make it match the front index
        return this.frontIndex == (this.rearIndex + 1) % this.queue.length;
    }

    /**
     * @return Length of the {@link WaitingQueue}.
     */
    public int getQueueLength() {
        return this.queue.length;
    }

    /**
     * This method will construct a new array.
     * This may be an expensive operation.
     * @return An array of {@link Customer} objects representing the {@link WaitingQueue} state.
     */
    public Customer[] getQueue() {
        // Copy the indices
        int front = this.frontIndex;
        int rear = this.rearIndex;

        Customer[] tempQueue = new Customer[this.getQueueLength()];
        int queueIndex = 0;

        while (front != -1) { // While not empty
            tempQueue[queueIndex] = this.queue[front];
            queueIndex++;

            if (front == rear) { // If queue is empty
                front = -1;
                rear = -1;
            } else {
                front = (front + 1) % this.getQueueLength(); // Increment with overflow
            }
        }

        return tempQueue;
    }

    /**
     * Search for {@link Customer}s with the given search term within their name.
     * Searches are case-insensitive.
     * The returned array has null holes in it.
     * This is useful to ensure that the position data is not lost.
     * @param searchTerm The search term that is checked.
     * @return An array of {@link Customer}s who match the criteria.
     */
    public Customer[] searchCustomer(String searchTerm) {
        Customer[] localQueue = this.getQueue();
        Customer[] tempQueue = new Customer[localQueue.length];

        for (int i = 0; i < tempQueue.length; i++) {
            if (localQueue[i] != null && localQueue[i].getFullName().toUpperCase().contains(searchTerm.toUpperCase())) {
                tempQueue[i] = localQueue[i];
            }
        }

        return tempQueue;
    }

    /**
     * @return A string representation of the {@link WaitingQueue} instance.
     */
    @Override
    public String toString() {
        StringBuilder state = new StringBuilder(String.format("%s%n%d%n", WAITINGQUEUE_START_MARK, this.getQueueLength()));

        int customersSaved = 0;

        for (Customer customer : this.getQueue()) {
            if (customer != null) {
                state.append(customer);
                customersSaved++;
            }
        }

        state.append(String.format("null%n".repeat(this.getQueueLength() - customersSaved)));

        return state.toString();
    }
}
