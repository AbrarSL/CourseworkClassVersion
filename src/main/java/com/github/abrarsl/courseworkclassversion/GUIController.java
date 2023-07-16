package com.github.abrarsl.courseworkclassversion;

import com.github.abrarsl.courseworkclassversion.exceptions.SelectionOutOfRangeException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GUIController {
    private FoodQueue[] queues;
    private WaitingQueue waitingQueue;

    @FXML
    protected HBox queueContainer;

    @FXML
    protected Label firstNameLabel;
    @FXML
    protected Label lastNameLabel;
    @FXML
    protected Label burgerLabel;

    @FXML
    protected TextField searchField;
    @FXML
    protected VBox searchResultContainer;

    @FXML
    protected void initialize() {
        this.queues = Main.getQueues();
        this.waitingQueue = Main.getWaitingQueue();
        this.queueContainer.getChildren().clear();

        this.constructQueues();
        this.constructWaitingQueues();
    }

    protected void constructQueues() {
        for (int i = 0; i < this.queues.length; i++) {
            VBox queueBox = this.createQueueBox("Queue " + i);

            try {
                for (int j = 0; j < this.queues[i].getQueueLength(); j++) {
                    Customer customer = this.queues[i].getCustomer(j);
                    Button customerButton = this.createCustomerButton(customer, i, j, this::handleCustomerAction);
                    queueBox.getChildren().add(customerButton);
                }
            } catch (SelectionOutOfRangeException ignored) {
            }

            this.queueContainer.getChildren().add(queueBox);
        }
    }

    protected void constructWaitingQueues() {
        VBox waitingBox = this.createQueueBox("Waiting Queue");

        for (int i = 0; i < this.waitingQueue.getQueueLength(); i++) {
            Customer customer = this.waitingQueue.getQueue()[i];
            Button customerButton = this.createCustomerButton(customer, -1, i, this::handleCustomerAction);
            waitingBox.getChildren().add(customerButton);
        }

        this.queueContainer.getChildren().add(waitingBox);
    }

    protected VBox createQueueBox(String title) {
        VBox queueBox = new VBox();
        queueBox.getChildren().add(new Label(title));
        queueBox.setPadding(new Insets(12, 12, 12, 12));
        queueBox.setSpacing(12);
        queueBox.setAlignment(Pos.TOP_CENTER);

        return queueBox;
    }

    protected Button createCustomerButton(Customer customer, int queueNo, int customerNo, EventHandler<ActionEvent> eventHandler) {
        Button customerButton = new Button(customer == null ? "X" : "O");
        customerButton.setId(String.format("%d%s%d", queueNo, Customer.INFO_DELIMITER, customerNo));
        customerButton.setDisable(customer == null);
        customerButton.setOnAction(eventHandler);

        return customerButton;
    }

    protected HBox createSearchResultRow(Customer customer, int queueNo, int customerNo, EventHandler<ActionEvent> eventHandler) {
        HBox customerContainer = new HBox();
        customerContainer.setAlignment(Pos.CENTER);
        customerContainer.setSpacing(12);
        Button customerButton = this.createCustomerButton(customer, queueNo, customerNo, eventHandler);
        Label customerLabel = new Label("Queue: " + queueNo + ", Position: " + customerNo);
        customerContainer.getChildren().addAll(customerButton, customerLabel);

        return customerContainer;
    }

    protected void handleCustomerAction(ActionEvent actionEvent) {
        Button customerButton = (Button) actionEvent.getSource();
        String[] idPayload = customerButton.getId().split(Customer.INFO_DELIMITER);
        int[] queueIndex = new int[idPayload.length];

        for (int i = 0; i < queueIndex.length; i++) {
            queueIndex[i] = Integer.parseInt(idPayload[i]);
        }

        if (queueIndex[0] < 0) {
            Customer customer = this.waitingQueue.getQueue()[queueIndex[1]];
            this.setCustomerInfo(customer);
        } else {
            try {
                Customer customer = this.queues[queueIndex[0]].getCustomer(queueIndex[1]);
                this.setCustomerInfo(customer);
            } catch (SelectionOutOfRangeException ignored) {
            }
        }
    }

    protected void setCustomerInfo(Customer customer) {
        this.firstNameLabel.setText(customer.getFirstName());
        this.lastNameLabel.setText(customer.getLastName());
        this.burgerLabel.setText(String.valueOf(customer.getBurgersRequired()));
    }

    @FXML
    protected void handleSearchAction() {
        this.searchResultContainer.getChildren().clear();
        String searchTerm = this.searchField.getText();
        this.searchField.setText("");

        for (int i = 0; i < this.queues.length; i++) {
            Customer[] foundCustomers = this.queues[i].searchCustomer(searchTerm);

            for (int j = 0; j < foundCustomers.length; j++) {
                if (foundCustomers[j] != null) {
                    HBox customerContainer = this.createSearchResultRow(foundCustomers[j], i, j, this::handleCustomerAction);
                    this.searchResultContainer.getChildren().add(customerContainer);
                }
            }
        }

        Customer[] foundWaitingCustomers = this.waitingQueue.searchCustomer(searchTerm);

        for (int i = 0; i < foundWaitingCustomers.length; i++) {
            if (foundWaitingCustomers[i] != null) {
                HBox customerContainer = this.createSearchResultRow(foundWaitingCustomers[i], -1, i, this::handleCustomerAction);
                this.searchResultContainer.getChildren().add(customerContainer);
            }
        }
    }
}
