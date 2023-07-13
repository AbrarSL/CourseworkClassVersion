package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static final String DECOR_CHARACTER = "*";
    private static final String FOODQUEUE_START_MARK = "FOODQUEUE_START";
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
                case "LPD", "107":
                    loadProgramData();
                    break;
                case "STK", "108":
                    viewBurgerStock();
                    break;
                case "AFS", "109":
                    addToBurgerStock();
                    break;
                case "IFQ", "110":
                    viewQueueIncome();
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

    private static int intInputPrompt(String prompt, int start, int end)
            throws SelectionOutOfRangeException, NumberFormatException {
        final int result = Integer.parseInt(inputPrompt(prompt));

        if (result < start || result >= end) {
            throw new SelectionOutOfRangeException(String.format("Range is %d to %d.", start, end - 1));
        }

        return result;
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
                110 or IFQ: View queue income information.
                999 or EXT: Exit the program.
                """;

        System.out.println(commands);
    }

    private static void displayHeader(String headerText) {
        final int headerLength = headerText.length() + HORIZONTAL_PADDING;
        final int titlePaddingLength = (headerLength - headerText.length() - 2) / 2;

        System.out.println(DECOR_CHARACTER.repeat(headerLength));
        System.out.println(String.format(
                "%s%s%s%s%s",
                DECOR_CHARACTER,
                " ".repeat(titlePaddingLength),
                headerText,
                " ".repeat(titlePaddingLength),
                DECOR_CHARACTER
        ));
        System.out.println(DECOR_CHARACTER.repeat(headerLength));
    }

    private static void displayQueueMenu() {
        displayHeader("Queue Selection");
        for (int i = 0; i < queues.length; i++) {
            System.out.printf(String.format("%d - Size %d.%n", i, queues[i].getQueueLength()));
        }
    }

    private static void displayQueues(FoodQueue[] queues) {
        final String headerText = "Cashiers (Queue View)";
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
        displayHeader("Add Customer");

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
        displayHeader("Remove Customer");
        displayQueueMenu();

        try {
            int queuePosition = intInputPrompt(
                    "Enter the queue number to remove customer from: ",
                    0,
                    queues.length
            );

            int customerPosition = intInputPrompt(
                    "Enter the customer position: ",
                    0,
                    queues[queuePosition].getQueueLength()
            );

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
        displayHeader("Remove Served Customer");
        displayQueueMenu();

        try {
            int queuePosition = intInputPrompt(
                    "Enter the queue number to remove customer from: ",
                    0,
                    queues.length
            );

            Customer customer = queues[queuePosition].serveCustomer();

            shouldSortCustomerList = true;
            System.out.printf(
                    "Customer %s was served %d items!%n",
                    customer.getFullName(),
                    customer.getBurgersRequired()
            );
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
        displayHeader("Store Program Data");

        System.out.printf("Saving data to file: %s%n", FILE_PATH);

        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            fileWriter.write(String.format(
                    "%d%n%d%n",
                    FoodQueue.getItemStock(),
                    queues.length
            ));

            for (FoodQueue queue : queues) {
                fileWriter.write(String.format(
                        "%s%n%s",
                        FOODQUEUE_START_MARK,
                        queue
                ));
            }

            fileWriter.flush();

            System.out.println("Data successfully written to file!");
        } catch (IOException exception) {
            System.out.println("File could not be created!");
        }
    }

    private static void loadProgramData() {
        displayHeader("Load Program Data");

        System.out.printf("Loading data from file: %s%n", FILE_PATH);

        try (Scanner fileReader = new Scanner(new File(FILE_PATH))) {
            if (!fileReader.hasNextInt()) {
                throw new InvalidFileDataException("Stock data not found!");
            }

            final int newFoodStock = Integer.parseInt(fileReader.nextLine());

            if (!fileReader.hasNextInt()) {
                throw new InvalidFileDataException("Number of queues not found!");
            }

            final int numberOfQueues = Integer.parseInt(fileReader.nextLine());

            final FoodQueue[] loadedQueues = new FoodQueue[numberOfQueues];

            for (int i = 0; i < loadedQueues.length; i++) {
                if (!fileReader.hasNextLine()) {
                    throw new InvalidFileDataException("FoodQueue information not found!");
                }

                String fileLine = fileReader.nextLine();

                if (!fileLine.equals(FOODQUEUE_START_MARK)) {
                    throw new InvalidFileDataException("FoodQueue data marker not found!");
                }

                if (!fileReader.hasNextInt()) {
                    throw new InvalidFileDataException("FoodQueue length information not found!");
                }

                final int queueLength = Integer.parseInt(fileReader.nextLine());

                if (!fileReader.hasNextInt()) {
                    throw new InvalidFileDataException("FoodQueue income information not found!");
                }

                final int queueIncome = Integer.parseInt(fileReader.nextLine());

                final Customer[] customers = new Customer[queueLength];

                for (int j = 0; j < customers.length; j++) {
                    if (!fileReader.hasNextLine()) {
                        throw new InvalidFileDataException("FoodQueue data ended unexpectedly!");
                    }

                    String customerData = fileReader.nextLine();

                    if (customerData.equals("null")) {
                        customers[j] = null;
                    } else {
                        String[] parsedInfo = customerData.split(Customer.INFO_DELIMITER);

                        Customer customer = new Customer(
                                parsedInfo[0],
                                parsedInfo[1],
                                Integer.parseInt(parsedInfo[2])
                        );

                        customers[j] = customer;
                    }
                }

                loadedQueues[i] = new FoodQueue(customers, queueIncome);
            }

            FoodQueue.setItemStock(newFoodStock);
            queues = loadedQueues;

            System.out.println("Data loaded successfully!");
        } catch (FileNotFoundException exception) {
            System.out.println("File was not found!");
        } catch (InvalidFileDataException exception) {
            System.out.println(exception.getMessage());
            System.out.println("Data was not loaded!");
        } catch (StockOutOfRange exception) {
            System.out.println("Loaded stock data is out of range!");
            System.out.println(exception.getMessage());
            System.out.println("Data was not loaded!");
        }
    }

    private static void viewBurgerStock() {
        displayHeader("Burger Stock");
        System.out.printf("Items: %d%n", FoodQueue.getItemStock());
    }

    private static void addToBurgerStock() {
        displayHeader("Add Burger Stock");

        System.out.printf("Current Stock: %d%n", FoodQueue.getItemStock());

        try {
            int newBurgerStock = FoodQueue.getItemStock() + Integer.parseInt(
                    inputPrompt("Enter the amount of burgers to add: ")
            );

            FoodQueue.setItemStock(newBurgerStock);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a positive number!");
        } catch (StockOutOfRange exception) {
            System.out.println("Stock is out of range! " + exception.getMessage());
        }
    }

    private static void viewQueueIncome() {
        displayHeader("View Queue Income");
        displayQueueMenu();

        try {
            int queueSelection = intInputPrompt("Enter the queue number: ", 0, queues.length);

            displayHeader(String.format("Queue %d Income", queueSelection));
            System.out.println(queues[queueSelection].getQueueIncome());
        } catch (NumberFormatException exception) {
            System.out.println("Please enter a number!");
        } catch (SelectionOutOfRangeException exception) {
            System.out.println("Queue number is out of range!");
            System.out.println(exception.getMessage());
        }
    }
}
