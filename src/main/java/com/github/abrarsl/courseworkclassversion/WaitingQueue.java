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

    @Override
    public String toString() {
        StringBuilder state = new StringBuilder(String.format("%s%n%d%n", WAITINGQUEUE_START_MARK, this.getQueueLength()));

        int customersSaved = 0;

        int front = this.frontIndex;
        int rear = this.rearIndex;

        while (front != -1) {
            state.append(this.queue[front]);
            customersSaved++;

            if (front == rear) {
                front = -1;
                rear = -1;
            } else {
                front = (front + 1) % this.getQueueLength();
            }
        }

        if (customersSaved < this.getQueueLength()) {
            state.append(String.format("null%n").repeat(this.getQueueLength() - customersSaved));
        }

        return state.toString();
    }
}
