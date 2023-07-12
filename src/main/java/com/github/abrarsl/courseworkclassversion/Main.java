package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static final String DECOR_CHARACTER = "*";
    private static final int HORIZONTAL_PADDING = 10;
    private static final String FILE_PATH = "./programState.txt";
    private static FoodQueue[] queues;
    private static Customer[] sortedCustomerList;
    private static boolean shouldSortCustomerList = true;

    public static void main(String[] args) {
        queues = initQueues(new int[]{2, 3, 5});
        displayCommands();

        while (true) {
            displayStockWarning();
            // All commands are converted to uppercase for easier parsing
            String command = inputPrompt("Enter a command (\"H\" or 000 for Help Menu): ").strip().toUpperCase();

            // Option selection
            switch (command) {
                case "H", "000":
                    displayCommands();
                    break;
                case "VFQ", "100":
                    displayQueues(queues);
                    break;
                case "VEQ", "101":
                    viewEmptyQueues();
                    break;
                case "ACQ", "102":
                    addCustomerToQueue();
                    break;
                case "RCQ", "103":
                    removeCustomerFromQueue();
                    break;
                case "PCQ", "104":
                    removeServedCustomer();
                    break;
                case "VCS", "105":
                    viewSortedCustomers();
                    break;
                case "SPD", "106":
                    storeProgramData();
                    break;
                /*case "LPD", "107":
                    loadProgramData();
                    break;*/
                case "STK", "108":
                    viewBurgerStock();
                    break;
                case "AFS", "109":
                    addToBurgerStock();
                    break;
                case "EXT", "999":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Unknown Command!");
            }
        }
    }

    private static FoodQueue[] initQueues(int[] queueLayout) {
        FoodQueue[] tempQueue = new FoodQueue[queueLayout.length];

        for (int i = 0; i < tempQueue.length; i++) {
            tempQueue[i] = new FoodQueue(queueLayout[i]);
        }

        return tempQueue;
    }

    private static String inputPrompt(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    private static void sortCustomers() {
        int totalCustomersLength = 0;

        for (FoodQueue queue : queues) {
            totalCustomersLength += queue.getQueueLength();
        }

        Customer[] tempList = new Customer[totalCustomersLength];
        int index = 0;

        for (FoodQueue queue : queues) {
            for (int i = 0; i < queue.getQueueLength(); i++) {
                try {
                    if (queue.getCustomer(i) == null) {
                        break;
                    }

                    tempList[index++] = queue.getCustomer(i);
                } catch (Exception ignored) {
                }
            }
        }

        if (index < totalCustomersLength) {
            sortedCustomerList = new Customer[index];

            for (int i = 0; i < sortedCustomerList.length; i++) {
                sortedCustomerList[i] = tempList[i];
            }
        } else {
            sortedCustomerList = tempList;
        }

        bubbleSortCustomers(sortedCustomerList);
        shouldSortCustomerList = false;
    }

    private static void bubbleSortCustomers(Customer[] array) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < array.length - 1; i++) {
                if (array[i].getFullName().compareTo(array[i + 1].getFullName()) > 0) {
                    Customer temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    swapped = true;
                }
            }
        }
    }

    private static void displayCommands() {
        displayHeader("Foodies Fave Food Center");

        final String commands = """
                000 or H: View help menu.
                100 or VFQ: View all queues.
                101 or VEQ: View all empty queues.
                102 or ACQ: Add a customer to a queue.
                103 or RCQ: Remove a customer from a specific location on a queue.
                104 or PCQ: Remove a served customer.
                105 or VCS: View customers in alphabetical order.
                106 or SPD: Store program data into file.
                107 or LPD: Load program data from file.
                108 or STK: View remaining burger stock.
                109 or AFS: Add burgers to stock.
                999 or EXT: Exit the program.
                """;

        System.out.println(commands);
    }

    private static void displayHeader(String headerText) {
        final int headerLength = headerText.length() + HORIZONTAL_PADDING;
        final int titlePaddingLength = (headerLength - headerText.length() - 2) / 2;

        System.out.println(DECOR_CHARACTER.repeat(headerLength));
        System.out.println(DECOR_CHARACTER + " ".repeat(titlePaddingLength) + headerText + " ".repeat(titlePaddingLength) + DECOR_CHARACTER);
        System.out.println(DECOR_CHARACTER.repeat(headerLength));
    }

    private static void displayQueues(FoodQueue[] queues) {
        final String headerText = "Cashiers";
        final int longestQueueLength = queues[2].getQueueLength();
        final int headerLength = headerText.length() + HORIZONTAL_PADDING;
        final int paddingLength = ((headerLength / queues.length) - 1) / 2;

        displayHeader(headerText);

        for (int i = 0; i < longestQueueLength; i++) {

            for (FoodQueue queue : queues) {
                System.out.print(" ".repeat(paddingLength));

                try {
                    if (queue.getQueueLength() <= i) {
                        System.out.print(" ");
                    } else if (queue.getCustomer(i) == null) {
                        System.out.print("O");
                    } else {
                        System.out.print("X");
                    }

                    System.out.print(" ".repeat(paddingLength));
                } catch (Exception ignored) {
                }
            }

            System.out.println();
        }
    }

    private static void displayStockWarning() {
        if (FoodQueue.isStockLow()) {
            System.out.printf("Low stock level! %d items left!%n", FoodQueue.getItemStock());
        }
    }

    private static void viewEmptyQueues() {
        FoodQueue[] availableQueues = new FoodQueue[queues.length];

        for (int i = 0; i < queues.length; i++) {
            if (queues[i].isQueueFull()) {
                availableQueues[i] = new FoodQueue(0);
            } else {
                availableQueues[i] = queues[i];
            }
        }

        displayQueues(availableQueues);
    }

    private static int tryAddCustomer(Customer customer) throws FullQueueException {
        for (int i = 0; i < queues.length; i++) {
            try {
                queues[i].addCustomer(customer);
                return i;
            } catch (FullQueueException exception) {
                System.out.printf("Queue %d full.%n", i);
            }
        }

        throw new FullQueueException();
    }

    private static void addCustomerToQueue() {
        try {
            String customerFirstName = inputPrompt("Enter the customer's first name: ");
            String customerLastName = inputPrompt("Enter the customer's last name: ");
            int customerBurgerNumber = Integer.parseInt(inputPrompt("Enter the number of burgers needed: "));

            if (customerBurgerNumber < 0) {
                throw new NumberFormatException();
            }

            Customer customer = new Customer(customerFirstName, customerLastName, customerBurgerNumber);

            int queueNumber = tryAddCustomer(customer);

            shouldSortCustomerList = true;
            System.out.printf("Customer %s, added to queue %d!%n", customer.getFullName(), queueNumber);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a positive number!");
        } catch (FullQueueException exception) {
            System.out.println("All queues full! Customer could not be added!");
        }
    }

    private static void removeCustomerFromQueue() {
        try {
            int queuePosition = Integer.parseInt(inputPrompt("Enter the queue number to remove customer from: "));

            if (queuePosition < 0 || queuePosition >= queues.length) {
                throw new SelectionOutOfRangeException(String.format("Range is %d to %d.", 0, queues.length - 1));
            }

            int customerPosition = Integer.parseInt(inputPrompt("Enter the customer position: "));

            try {
                Customer customer = queues[queuePosition].removeCustomer(customerPosition);

                shouldSortCustomerList = true;
                System.out.printf("Removed customer %s!%n", customer.getFirstName());
            } catch (SelectionOutOfRangeException exception) {
                System.out.println("Incorrect customer position! " + exception.getMessage());
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a number!");
        } catch (SelectionOutOfRangeException exception) {
            System.out.println("Incorrect queue number! " + exception.getMessage());
        } catch (CustomerNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void removeServedCustomer() {
        try {
            int queuePosition = Integer.parseInt(inputPrompt("Enter the queue number to remove customer from: "));

            if (queuePosition < 0 || queuePosition >= queues.length) {
                throw new SelectionOutOfRangeException(String.format("Range is %d to %d.", 0, queues.length - 1));
            }

            Customer customer = queues[queuePosition].serveCustomer();

            shouldSortCustomerList = true;
            System.out.printf("Customer %s was served %d items!%n", customer.getFullName(), customer.getBurgersRequired());
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a number!");
        } catch (SelectionOutOfRangeException exception) {
            System.out.println("Incorrect queue number! " + exception.getMessage());
        } catch (InsufficientStockException exception) {
            System.out.printf("Insufficient stock level! Customer requires %s items!%n", exception.getMessage());
        } catch (CustomerNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void viewSortedCustomers() {
        if (shouldSortCustomerList) {
            sortCustomers();
        }

        String headerText = "Customers (Sorted Alphabetically)";
        displayHeader(headerText);

        for (Customer customer : sortedCustomerList) {
            System.out.println("First Name: " + customer.getFirstName());
            System.out.println("Last Name: " + customer.getLastName());
            System.out.println("Burgers Required: " + customer.getBurgersRequired());
            System.out.println(DECOR_CHARACTER.repeat(headerText.length() + HORIZONTAL_PADDING));
        }
    }

    private static void storeProgramData() {
        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            fileWriter.write(FoodQueue.getItemStock());

            for (FoodQueue queue : queues) {
                fileWriter.write(String.format("%n%s", queue.stringifyState()));
            }

            fileWriter.flush();
        } catch (IOException exception) {
            System.out.println("File could not be created!");
            System.out.println("Error: " + exception.getMessage());
        }
    }

    private static void viewBurgerStock() {
        displayHeader("Burger Stock");
        System.out.printf("Items: %d%n", FoodQueue.getItemStock());
    }

    private static void addToBurgerStock() {
        try {
            int newBurgerStock = FoodQueue.getItemStock() + Integer.parseInt(inputPrompt("Enter the amount of burgers to add: "));

            FoodQueue.setItemStock(newBurgerStock);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a positive number!");
        } catch (StockOutOfRange exception) {
            System.out.println("Stock is out of range! " + exception.getMessage());
        }
    }
}
