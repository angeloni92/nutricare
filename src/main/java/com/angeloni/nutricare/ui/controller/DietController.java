package com.angeloni.nutricare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Controller;
import com.angeloni.nutricare.service.DietService;
import com.angeloni.nutricare.dto.DietDetailDto;

/**
 * Diet management screen controller for JavaFX
 */
@Controller
public class DietController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TableView<DietDetailDto> dietTable;

    @FXML
    private TableColumn<DietDetailDto, String> clientColumn;

    @FXML
    private TableColumn<DietDetailDto, String> dateColumn;

    @FXML
    private TableColumn<DietDetailDto, String> statusColumn;

    @FXML
    private ComboBox<String> filterCombo;

    @FXML
    private Button viewButton;

    @FXML
    private Button generateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private final DietService dietService;

    public DietController(DietService dietService) {
        this.dietService = dietService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupUI();
        setupFilters();
        loadDiets();
    }

    private void setupTableColumns() {
        clientColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty("")
        );
        dateColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty("")
        );
        statusColumn.setCellValueFactory(cellData ->
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

        viewButton.setStyle(buttonStyle);
        generateButton.setStyle(buttonStyle.replace("#007bff", "#28a745"));
        deleteButton.setStyle(buttonStyle.replace("#007bff", "#dc3545"));
        backButton.setStyle(buttonStyle.replace("#007bff", "#6c757d"));

        rootPane.setStyle("-fx-background-color: #f8f9fa;");
    }

    private void setupFilters() {
        ObservableList<String> filters = FXCollections.observableArrayList(
            "All", "Active", "Completed", "Archived"
        );
        filterCombo.setItems(filters);
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> loadDiets());
    }

    private void loadDiets() {
        try {
            var diets = dietService.getAllDiets(); // TODO: replace DietDetailDto with proper result DTO
            ObservableList<DietDetailDto> data = FXCollections.observableArrayList(diets);
            dietTable.setItems(data);
        } catch (Exception e) {
            showError("Failed to load diets: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewDiet() {
        DietDetailDto selected = dietTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a diet to view");
            return;
        }
        // TODO: Open diet detail view
    }

    @FXML
    private void handleGenerateDiet() {
        // TODO: Navigate to diet generator
    }

    @FXML
    private void handleDeleteDiet() {
        DietDetailDto selected = dietTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a diet to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Diet");
        alert.setContentText("Are you sure you want to delete this diet?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                dietService.deleteDiet(selected.getId());
                loadDiets();
                showInfo("Diet deleted successfully");
            } catch (Exception e) {
                showError("Failed to delete diet: " + e.getMessage());
            }
        }
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

