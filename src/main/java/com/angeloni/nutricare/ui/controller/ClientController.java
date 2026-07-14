package com.angeloni.nutricare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Controller;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.dto.ClientDto;

/**
 * Client management screen controller for JavaFX
 */
@Controller
public class ClientController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TableView<ClientDto> clientTable;

    @FXML
    private TableColumn<ClientDto, String> nameColumn;

    @FXML
    private TableColumn<ClientDto, String> emailColumn;

    @FXML
    private TableColumn<ClientDto, String> phoneColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button addClientButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupUI();
        loadClients();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getName() != null ? cellData.getValue().getName() : ""
            )
        );
        emailColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty("")
        );
        phoneColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty("")
        );
    }

    private void setupUI() {
        String buttonStyle = """
            -fx-font-size: 12;
            -fx-padding: 10;
            -fx-background-color: #007bff;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """;

        addClientButton.setStyle(buttonStyle);
        editButton.setStyle(buttonStyle.replace("#007bff", "#28a745"));
        deleteButton.setStyle(buttonStyle.replace("#007bff", "#dc3545"));
        backButton.setStyle(buttonStyle.replace("#007bff", "#6c757d"));

        rootPane.setStyle("-fx-background-color: #f8f9fa;");
    }

    private void loadClients() {
        try {
            var clients = clientService.getClients();
            ObservableList<ClientDto> data = FXCollections.observableArrayList(clients);
            clientTable.setItems(data);
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddClient() {
        // TODO: Open add client dialog
    }

    @FXML
    private void handleEditClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a client to edit");
            return;
        }
        // TODO: Open edit client dialog
    }

    @FXML
    private void handleDeleteClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a client to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Client");
        alert.setContentText("Are you sure you want to delete this client?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                clientService.deleteClientById(selected.getId());
                loadClients();
                showInfo("Client deleted successfully");
            } catch (Exception e) {
                showError("Failed to delete client: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadClients();
            return;
        }
        // TODO: Implement search functionality
    }

    @FXML
    private void handleBack() {
        // TODO: Navigate back to dashboard
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

