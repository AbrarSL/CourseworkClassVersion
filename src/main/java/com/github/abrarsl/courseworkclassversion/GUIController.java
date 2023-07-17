package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.SelectionOutOfRangeException;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class GUIController {
    private FoodQueue[] queues;
    private WaitingQueue waitingQueue;

    @FXML
    protected HBox queueContainer;
    @FXML
    protected TextField searchField;
    @FXML
    protected VBox searchResultContainer;

    /**
     * This method is automatically called by JavaFX when the FXML file is loaded.
     * All initialization work should be handled here.
     * This method is also used as a callback for reinitializing the GUI after a change in the data structures.
     */
    @FXML
    protected void initialize() {
        // Copy reference to data
        this.queues = TUIController.getQueues();
        this.waitingQueue = TUIController.getWaitingQueue();

        this.queueContainer.getChildren().clear();

        this.constructQueues();
        this.constructWaitingQueues();
    }

    /**
     * This method will construct queue widgets and add them to the {@link GUIController#queueContainer}.
     */
    protected void constructQueues() {
        ObservableList<Node> queueContainerChildren = this.queueContainer.getChildren();

        for (int i = 0; i < this.queues.length; i++) { // Get each FoodQueue
            VBox queueBox = this.createQueueBox("Queue " + i);
            ObservableList<Node> queueBoxChildren = queueBox.getChildren();

            try {
                for (int j = 0; j < this.queues[i].getQueueLength(); j++) { // Create a Label for each Customer
                    Customer customer = this.queues[i].getCustomer(j);
                    Label customerLabel = this.createCustomerLabel(customer, i, j, this::handleCustomerAction);
                    queueBoxChildren.add(customerLabel);
                }
            } catch (SelectionOutOfRangeException ignored) {
            }

            queueContainerChildren.add(queueBox);
        }
    }

    /**
     * This method will construct widgets to represent a {@link WaitingQueue}
     * and add them to {@link GUIController#queueContainer}.
     */
    protected void constructWaitingQueues() {
        VBox waitingBox = this.createQueueBox("Waiting Queue");
        ObservableList<Node> waitingBoxChildren = waitingBox.getChildren();

        for (int i = 0; i < this.waitingQueue.getQueueLength(); i++) { // Construct a Label for each Customer
            Customer customer = this.waitingQueue.getQueue()[i];
            Label customerLabel = this.createCustomerLabel(customer, -1, i, this::handleCustomerAction);
            waitingBoxChildren.add(customerLabel);
        }

        this.queueContainer.getChildren().add(waitingBox);
    }

    /**
     * Creates a {@link VBox} container with some specific styling and a title {@link Label}.
     * @param title The title of the queue box.
     * @return A {@link VBox} that has a title {@link Label}.
     */
    protected VBox createQueueBox(String title) {
        VBox queueBox = new VBox();
        Label queueLabel = new Label(title);
        queueBox.getChildren().add(queueLabel);
        queueBox.setPadding(new Insets(12, 12, 12, 12));
        queueBox.setSpacing(12);
        queueBox.setAlignment(Pos.TOP_CENTER);

        return queueBox;
    }

    /**
     * Creates a clickable {@link Label} with the customer details added to it.
     * @param customer The {@link Customer} to construct the {@link Label} for.
     * @param queueNo The queue number of the {@link Customer}.
     * @param customerNo The index of the {@link Customer}.
     * @param eventHandler An event handler that will fire when the label is clicked.
     * @return A {@link Label} object with customer styling.
     */
    protected Label createCustomerLabel(Customer customer, int queueNo, int customerNo, EventHandler<MouseEvent> eventHandler) {
        Label customerLabel = new Label(String.format(
                "%d. %s",
                customerNo,
                (customer == null ? "X" : customer.getFullName())
        ));

        // The object id is used to index the queues
        customerLabel.setId(String.format("%d%s%d", queueNo, Customer.INFO_DELIMITER, customerNo));
        // Set custom css classes
        customerLabel.getStyleClass().addAll("customer-label", customer == null ? "vacant-label" : "");
        customerLabel.setDisable(customer == null);
        customerLabel.setTextAlignment(TextAlignment.CENTER);
        customerLabel.setOnMouseClicked(eventHandler);

        return customerLabel;
    }

    /**
     * Handles click actions on customer {@link Label} objects.
     * @param mouseEvent The event object.
     */
    protected void handleCustomerAction(MouseEvent mouseEvent) {
        Label customerLabel = (Label) mouseEvent.getSource();
        String[] idPayload = customerLabel.getId().split(Customer.INFO_DELIMITER);
        int[] queueIndex = new int[idPayload.length];

        for (int i = 0; i < queueIndex.length; i++) {
            queueIndex[i] = Integer.parseInt(idPayload[i]);
        }

        // Get customer from appropriate queue
        if (queueIndex[0] < 0) {
            Customer customer = this.waitingQueue.getQueue()[queueIndex[1]];
            this.showCustomerInfo(customer, queueIndex[0], queueIndex[1]);
        } else {
            try {
                Customer customer = this.queues[queueIndex[0]].getCustomer(queueIndex[1]);
                this.showCustomerInfo(customer, queueIndex[0], queueIndex[1]);
            } catch (SelectionOutOfRangeException ignored) {
            }
        }
    }

    /**
     * Create an Alert and populate it with the information from the given {@link Customer} object.
     * @param customer The {@link Customer} object that is used.
     * @param queueNo The queue number of the {@link Customer}.
     * @param positionNo The index of the {@link Customer}.
     */
    protected void showCustomerInfo(Customer customer, int queueNo, int positionNo) {
        String customerInfo = String.format(
                "First Name: %s%nLast Name: %s%nBurgers Needed: %s%nQueue: %s%nPosition: %s",
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBurgersRequired(),
                queueNo,
                positionNo
        );
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, customerInfo);
        infoAlert.show();
    }

    /**
     * Handle the search event.
     * This method will search all {@link FoodQueue} objects as well as the {@link WaitingQueue} object for matches.
     * The matches are then shown in the GUI via {@link GUIController#searchResultContainer}.
     */
    @FXML
    protected void handleSearchAction() {
        ObservableList<Node> resultContainerChildren = this.searchResultContainer.getChildren();
        resultContainerChildren.clear();

        String searchTerm = this.searchField.getText();
        this.searchField.setText("");

        for (int i = 0; i < this.queues.length; i++) { // Search FoodQueue objects
            Customer[] foundCustomers = this.queues[i].searchCustomer(searchTerm);

            for (int j = 0; j < foundCustomers.length; j++) {
                if (foundCustomers[j] != null) {
                    Label customerLabel = this.createCustomerLabel(foundCustomers[j], i, j, this::handleCustomerAction);
                    resultContainerChildren.add(customerLabel);
                }
            }
        }

        Customer[] foundWaitingCustomers = this.waitingQueue.searchCustomer(searchTerm);

        for (int i = 0; i < foundWaitingCustomers.length; i++) { // Search WaitingQueue
            if (foundWaitingCustomers[i] != null) {
                Label customerLabel = this.createCustomerLabel(foundWaitingCustomers[i], -1, i, this::handleCustomerAction);
                resultContainerChildren.add(customerLabel);
            }
        }
    }
}
