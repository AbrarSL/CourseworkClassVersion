package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TUIController {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final String DECOR_CHARACTER = "*";
    private static final int HORIZONTAL_PADDING = 10;
    private static final String FILE_PATH = "./programState.txt";
    private static FoodQueue[] queues;
    private static int[] queuesInsertionOrder;
    private static WaitingQueue waitingQueue;
    private static Customer[] sortedCustomerList;
    private static boolean shouldSortCustomerList = true;

    public static void main(String[] args) {
        queues = genQueues(new int[]{7, 5, 2}); // MUST be called before running program
        queuesInsertionOrder = genQueuesInsertionOrder(queues); // MUST be called before running the program
        waitingQueue = new WaitingQueue(5); // MUST be set before running program
        initGui(); // MUST be called before launching GUI
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
                case "GUI", "112":
                    startGui();
                    break;
                case "EXT", "999":
                    System.out.println("Exiting...");
                    deInitGui(); // Must be called, otherwise FX thread will block exit
                    return;
                default:
                    System.out.println("Unknown Command!");
            }
        }
    }

    /**
     * Expose internal data structure {@link TUIController#queues}.
     *
     * @return A {@link FoodQueue}[] reference that can be used to populate a GUI.
     */
    public static FoodQueue[] getQueues() {
        return queues;
    }

    /**
     * Expose internal data structure {@link TUIController#waitingQueue}.
     *
     * @return A {@link WaitingQueue} reference that can be used to populate a GUI.
     */
    public static WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    /**
     * Creates a {@link FoodQueue}[] object according to the given layout.
     *
     * @param queueLayout An array of lengths of each queue.
     * @return A reference to the newly created {@link FoodQueue}[] instance.
     */
    private static FoodQueue[] genQueues(int[] queueLayout) {
        FoodQueue[] tempQueue = new FoodQueue[queueLayout.length];

        for (int i = 0; i < tempQueue.length; i++) {
            tempQueue[i] = new FoodQueue(queueLayout[i]);
        }

        return tempQueue;
    }

    /**
     * Generate an array of indices that reflects the ascending order of the {@link FoodQueue}[]
     * object that is passed in.
     *
     * @param foodQueues The array that will have it's order generated.
     * @return An array of indices indicating the ascending order.
     */
    private static int[] genQueuesInsertionOrder(FoodQueue[] foodQueues) {
        int[] insertionOrder = new int[foodQueues.length];

        for (int i = 0; i < foodQueues.length; i++) {
            insertionOrder[i] = i;
        }

        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < insertionOrder.length - 1; i++) {
                if (foodQueues[insertionOrder[i]].getQueueLength() >
                        foodQueues[insertionOrder[i + 1]].getQueueLength()) {
                    int temp = insertionOrder[i];
                    insertionOrder[i] = insertionOrder[i + 1];
                    insertionOrder[i + 1] = temp;
                    swapped = true;
                }
            }
        }

        return insertionOrder;
    }

    /**
     * A helper method to show a prompt and get some input from the user.
     *
     * @param prompt The prompt that will be shown to the user.
     * @return The input received from the user.
     */
    private static String inputPrompt(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine();
    }

    /**
     * Prompt and get an integer value from the user. If any of the checks fails an exception may be thrown.
     *
     * @param prompt The prompt that will be shown to the user.
     * @param start  The start of the number range that will be accepted. Inclusive.
     * @param end    The end of the number range that will be accepted. Exclusive.
     * @return An int that is within the given range.
     * @throws SelectionOutOfRangeException Thrown when the input is out of the acceptable range.
     * @throws NumberFormatException        Thrown if a non-numeric value is entered.
     */
    private static int intInputPrompt(String prompt, int start, int end)
            throws SelectionOutOfRangeException, NumberFormatException {
        final int result = Integer.parseInt(inputPrompt(prompt));

        if (result < start || result >= end) {
            throw new SelectionOutOfRangeException(String.format("Range is %d to %d.", start, end - 1));
        }

        return result;
    }

    /**
     * A number of hardcoded validation cases are checked by this method.
     *
     * @param input A string that needs to be validated.
     * @return The validated string.
     * @throws InputValidationException The reason for the failure is passed in the exception message.
     */
    private static String validateString(String input) throws InputValidationException {
        if (input.contains(Customer.INFO_DELIMITER)) {
            throw new InputValidationException(String.format(
                    "'%s' delimiter character detected!",
                    Customer.INFO_DELIMITER
            ));
        }

        if (input.isEmpty()) {
            throw new InputValidationException("Empty string detected!");
        }

        if (input.equals("null")) {
            throw new InputValidationException("'null' detected!");
        }

        if (input.contains(String.format("%n"))) {
            throw new InputValidationException("Newline character detected!");
        }

        return input;
    }

    /**
     * Sorts all customers in food queues using bubble sort and sets the result in {@link TUIController#sortedCustomerList},
     * {@link TUIController#shouldSortCustomerList} is set to false after running this method.
     */
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

    /**
     * An implementation of the bubble sort algorithm.
     *
     * @param array The array that will be sorted in-place.
     */
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

    /**
     * Display all valid commands for the program.
     */
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
                112 or GUI: Start GUI.
                999 or EXT: Exit the program.
                """;

        System.out.println(commands);
    }

    /**
     * Displays the given text with decoration and padding as defined by
     * {@link TUIController#DECOR_CHARACTER} and {@link TUIController#HORIZONTAL_PADDING}.
     *
     * @param headerText
     */
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

    /**
     * Displays all the queues that can be selected.
     */
    private static void displayQueueMenu() {
        displayHeader("Queue Selection");
        for (int i = 0; i < queues.length; i++) {
            System.out.printf(String.format("%d - Size %d.%n", i, queues[i].getQueueLength()));
        }
    }

    /**
     * Displays the state of all the {@link FoodQueue} objects in the given array as well as
     * the state of the {@link TUIController#waitingQueue}.
     *
     * @param queues
     */
    private static void displayQueues(FoodQueue[] queues) {
        final String headerText = "Cashiers (Queue View)";
        final int longestQueueLength = queues[queuesInsertionOrder[queuesInsertionOrder.length - 1]].getQueueLength();
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

        StringBuilder waitingQueueString = new StringBuilder("Waiting Queue:");

        for (Customer customer : waitingQueue.getQueue()) {
            waitingQueueString.append(customer == null ? " O" : " X");
        }

        System.out.println(waitingQueueString.toString());

        System.out.println("X - Occupied, 0 - Not Occupied");
    }

    /**
     * Display a warning if the {@link FoodQueue#isStockLow} indicates that stock is low.
     */
    private static void displayStockWarning() {
        if (FoodQueue.isStockLow()) {
            System.out.printf("Low stock level! %d items left!%n", FoodQueue.getItemStock());
        }
    }

    /**
     * Uses {@link TUIController#displayQueues} to display {@link FoodQueue} that are not full.
     */
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

    /**
     * Try adding a customer to each {@link FoodQueue} object in {@link TUIController#queues}.
     * The {@link TUIController#queuesInsertionOrder} array is used to get the indexing order.
     * This method therefore always tries to add from the shortest to the longest queue.
     *
     * @param customer The customer to add to a queue.
     * @return The number of the queue that the customer was added to. -1 if {@link TUIController#waitingQueue} was used.
     * @throws FullQueueException Is thrown only if all queues and the {@link TUIController#waitingQueue} are full.
     */
    private static int tryAddCustomer(Customer customer) throws FullQueueException {
        for (int i : queuesInsertionOrder) {
            try {
                queues[i].addCustomer(customer);
                return i;
            } catch (FullQueueException exception) {
                System.out.printf("Queue %d full.%n", i);
            }
        }

        waitingQueue.enqueue(customer);
        return -1;
    }

    /**
     * Attempts to add a customer from the {@link TUIController#waitingQueue} into a {@link FoodQueue}.
     * Only call this method after removing a customer from any {@link FoodQueue} that is held in {@link TUIController#queues}.
     *
     * @throws FullQueueException
     */
    private static void tryAddCustomerFromWaiting() throws FullQueueException {
        try {
            if (!waitingQueue.isQueueEmpty()) {
                Customer waitingCustomer = waitingQueue.dequeue();
                int queueNumber = tryAddCustomer(waitingCustomer);
                System.out.printf(
                        "Customer %s from waiting queue added to queue %d!%n",
                        waitingCustomer.getFullName(),
                        queueNumber
                );
            }
        } catch (CustomerNotFoundException ignored) {
        }
    }

    /**
     * Prompt the user for customer info and attempt the customer to the queue.
     * This method will set {@link TUIController#shouldSortCustomerList} to true.
     */
    private static void addCustomerToQueue() {
        displayHeader("Add Customer");

        try {
            String customerFirstName = validateString(inputPrompt("Enter the customer's first name: "));
            String customerLastName = validateString(inputPrompt("Enter the customer's last name: "));
            int customerBurgerNumber = Integer.parseInt(inputPrompt("Enter the number of burgers needed: "));

            if (customerBurgerNumber < 0) {
                throw new NumberFormatException();
            }

            Customer customer = new Customer(customerFirstName, customerLastName, customerBurgerNumber);

            int queueNumber = tryAddCustomer(customer);
            shouldSortCustomerList = true;

            String queueName = "queue " + queueNumber;

            if (queueNumber < 0) {
                queueName = "waiting queue";
            }

            System.out.printf("Customer %s, added to %s!%n", customer.getFullName(), queueName);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a positive number!");
        } catch (FullQueueException exception) {
            System.out.println("All queues full! Customer could not be added!");
        } catch (InputValidationException exception) {
            System.out.println("Input validation failed!");
            System.out.println("Reason: " + exception.getMessage());
        }
    }

    /**
     * Prompts the user for a position and removes a customer.
     * This method will set {@link TUIController#shouldSortCustomerList} to true.
     * This method also calls {@link TUIController#tryAddCustomerFromWaiting()}.
     */
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

                tryAddCustomerFromWaiting();
            } catch (SelectionOutOfRangeException exception) {
                System.out.println("Incorrect customer position! " + exception.getMessage());
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a number!");
        } catch (SelectionOutOfRangeException exception) {
            System.out.println("Incorrect queue number! " + exception.getMessage());
        } catch (CustomerNotFoundException exception) {
            System.out.println(exception.getMessage());
        } catch (FullQueueException ignored) {
        }
    }

    /**
     * Removes a customer from the selected queue if there is enough stock to serve them.
     * This method will call {@link FoodQueue#setItemStock(int)}.
     * This method will set {@link TUIController#shouldSortCustomerList} to true.
     * This method also calls {@link TUIController#tryAddCustomerFromWaiting()}.
     */
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

            tryAddCustomerFromWaiting();
        } catch (NumberFormatException exception) {
            System.out.println("Invalid Input! Enter a number!");
        } catch (SelectionOutOfRangeException exception) {
            System.out.println("Incorrect queue number! " + exception.getMessage());
        } catch (InsufficientStockException exception) {
            System.out.printf("Insufficient stock level! Customer requires %s items!%n", exception.getMessage());
        } catch (CustomerNotFoundException exception) {
            System.out.println(exception.getMessage());
        } catch (FullQueueException ignored) {
        }
    }

    /**
     * Show all the customers stored in {@link TUIController#sortedCustomerList}.
     * if {@link TUIController#shouldSortCustomerList} is true this method will call {@link TUIController#sortCustomers()}
     * before displaying anything.
     */
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

    /**
     * Stores program data as text at {@link TUIController#FILE_PATH}.
     */
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
                fileWriter.write(queue.toString());
            }

            fileWriter.write(waitingQueue.toString());

            fileWriter.flush();

            System.out.println("Data successfully written to file!");
        } catch (IOException exception) {
            System.out.println("File could not be created!");
        }
    }

    /**
     * Load data from a file at {@link TUIController#FILE_PATH}.
     * May mutate {@link TUIController#queues} and {@link TUIController#waitingQueue}.
     */
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

                if (!fileLine.equals(FoodQueue.FOODQUEUE_START_MARK)) {
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

            String fileLine = fileReader.nextLine();

            if (!fileLine.equals(WaitingQueue.WAITINGQUEUE_START_MARK)) {
                throw new InvalidFileDataException("Waiting queue marker not found!");
            }

            if (!fileReader.hasNextInt()) {
                throw new InvalidFileDataException("Waiting queue length info not found!");
            }

            int waitingQueueLength = Integer.parseInt(fileReader.nextLine());

            Customer[] loadedWaitingQueue = new Customer[waitingQueueLength];

            for (int j = 0; j < loadedWaitingQueue.length; j++) {
                if (!fileReader.hasNextLine()) {
                    throw new InvalidFileDataException("Waiting queue data ended unexpectedly!");
                }

                String customerData = fileReader.nextLine();

                if (customerData.equals("null")) {
                    loadedWaitingQueue[j] = null;
                } else {
                    String[] parsedInfo = customerData.split(Customer.INFO_DELIMITER);

                    Customer customer = new Customer(
                            parsedInfo[0],
                            parsedInfo[1],
                            Integer.parseInt(parsedInfo[2])
                    );

                    loadedWaitingQueue[j] = customer;
                }
            }

            FoodQueue.setItemStock(newFoodStock);
            queues = loadedQueues;
            queuesInsertionOrder = genQueuesInsertionOrder(queues);
            waitingQueue = new WaitingQueue(loadedWaitingQueue);

            System.out.println("Data loaded successfully!");
        } catch (FileNotFoundException exception) {
            System.out.println("File was not found!");
        } catch (InvalidFileDataException exception) {
            System.out.println(exception.getMessage());
            System.out.println("Data was not loaded!");
        } catch (StockOutOfRangeException exception) {
            System.out.println("Loaded stock data is out of range!");
            System.out.println(exception.getMessage());
            System.out.println("Data was not loaded!");
        }
    }

    /**
     * View the burger stock that remains in the {@link FoodQueue} class.
     */
    private static void viewBurgerStock() {
        displayHeader("Burger Stock");
        System.out.printf("Items: %d%n", FoodQueue.getItemStock());
    }

    /**
     * Attempt to edit the stock of all the {@link FoodQueue}.
     */
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
        } catch (StockOutOfRangeException exception) {
            System.out.println("Stock is out of range! " + exception.getMessage());
        }
    }

    /**
     * View income for any selected {@link FoodQueue} in {@link TUIController#queues}.
     */
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

    /**
     * Initialize the JavaFX platform.
     * This needs to be called before any other GUI code is run.
     */
    private static void initGui() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    /**
     * Shutdown the internal JavaFX application thread and allow the program to exit.
     */
    private static void deInitGui() {
        Platform.exit();
    }

    /**
     * Create a stage and load the viewer.
     * This method will run actual widget creation on the FX application thread.
     * This method does not block.
     */
    private static void startGui() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(TUIController.class.getResource("queue-viewer.fxml"));

                Scene primaryScene = new Scene(fxmlLoader.load());
                Stage primaryStage = new Stage();

                primaryStage.setScene(primaryScene);
                primaryStage.setTitle("Queue Viewer");
                primaryStage.show();
            } catch (IOException ignored) {
            }
        });
    }
}
