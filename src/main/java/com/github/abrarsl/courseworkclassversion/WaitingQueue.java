package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.CustomerNotFoundException;
import com.github.abrarsl.courseworkclassversion.exceptions.FullQueueException;

public class WaitingQueue {
    public static final String WAITINGQUEUE_START_MARK = "WAITINGQUEUE_START";
    private final Customer[] queue;
    private int frontIndex;
    private int rearIndex;

    public WaitingQueue(int length) {
        this.queue = new Customer[length];
        this.frontIndex = -1;
        this.rearIndex = -1;
    }

    public WaitingQueue(Customer[] queue) {
        this.queue = queue;

        if (this.queue[0] == null) {
            this.frontIndex = -1;
            this.rearIndex = -1;
        } else {
            this.frontIndex = 0;
            this.rearIndex = this.queue.length - 1;

            for (int i = 0; i < this.queue.length; i++) {
                if (this.queue[i] == null) {
                    this.rearIndex = i - 1;
                }
            }
        }
    }

    public void enqueue(Customer customer) throws FullQueueException {
        if (this.isQueueFull()) {
            throw new FullQueueException();
        }

        if (this.frontIndex == -1) {
            this.frontIndex = 0;
        }

        this.rearIndex = (this.rearIndex + 1) % this.queue.length;
        this.queue[this.rearIndex] = customer;
    }

    public Customer dequeue() throws CustomerNotFoundException {
        if (this.isQueueEmpty()) {
            throw new CustomerNotFoundException();
        }

        Customer customer = this.queue[this.frontIndex];
        this.queue[this.frontIndex] = null;

        if (this.frontIndex == this.rearIndex) {
            this.frontIndex = -1;
            this.rearIndex = -1;
        } else {
            this.frontIndex = (this.frontIndex + 1) % this.queue.length;
        }

        return customer;
    }

    public boolean isQueueEmpty() {
        return this.frontIndex == -1;
    }

    private boolean isQueueFull() {
        return this.frontIndex == (this.rearIndex + 1) % this.queue.length;
    }

    public int getQueueLength() {
        return this.queue.length;
    }

    public Customer[] getQueue() {
        int front = this.frontIndex;
        int rear = this.rearIndex;

        Customer[] tempQueue = new Customer[this.getQueueLength()];
        int queueIndex = 0;

        while (front != -1) {
            tempQueue[queueIndex] = this.queue[front];
            queueIndex++;

            if (front == rear) {
                front = -1;
                rear = -1;
            } else {
                front = (front + 1) % this.getQueueLength();
            }
        }

        return tempQueue;
    }

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
