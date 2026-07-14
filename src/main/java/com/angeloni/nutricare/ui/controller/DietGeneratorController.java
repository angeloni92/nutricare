package com.angeloni.nutricare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Controller;
import com.angeloni.nutricare.service.DietGeneratorService;
import com.angeloni.nutricare.dto.DietRequestDto;

/**
 * Diet Generator (AI) screen controller for JavaFX
 */
@Controller
public class DietGeneratorController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox formVBox;

    @FXML
    private ComboBox<String> clientCombo;

    @FXML
    private ComboBox<String> aiModelCombo;

    @FXML
    private ComboBox<String> primaryGoalCombo;

    @FXML
    private ComboBox<String> dietaryPreferenceCombo;

    @FXML
    private ComboBox<String> activityLevelCombo;

    @FXML
    private Spinner<Integer> mealsPerDaySpinner;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button generateButton;

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    private final DietGeneratorService dietGeneratorService;

    public DietGeneratorController(DietGeneratorService dietGeneratorService) {
        this.dietGeneratorService = dietGeneratorService;
    }

    @FXML
    public void initialize() {
        setupUI();
        setupComboBoxes();
        setupSpinner();
    }

    private void setupUI() {
        String buttonStyle = """
            -fx-font-size: 12;
            -fx-padding: 10;
            -fx-background-color: #28a745;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """;

        generateButton.setStyle(buttonStyle);
        backButton.setStyle(buttonStyle.replace("#28a745", "#6c757d"));

        formVBox.setStyle("""
            -fx-padding: 20;
            -fx-spacing: 15;
            -fx-background-color: #f8f9fa;
        """);

        progressBar.setVisible(false);
        statusLabel.setVisible(false);

        rootPane.setStyle("-fx-background-color: #ffffff;");
    }

    private void setupComboBoxes() {
        // AI Model options
        ObservableList<String> aiModels = FXCollections.observableArrayList(
            "GPT-4", "GPT-3.5", "Claude", "Gemini"
        );
        aiModelCombo.setItems(aiModels);
        aiModelCombo.setValue("GPT-4");

        // Primary Goal options
        ObservableList<String> primaryGoals = FXCollections.observableArrayList(
            "Weight Loss", "Muscle Gain", "Maintenance", "Athletic Performance", "Wellness"
        );
        primaryGoalCombo.setItems(primaryGoals);
        primaryGoalCombo.setValue("Wellness");

        // Dietary Preference options
        ObservableList<String> dietaryPrefs = FXCollections.observableArrayList(
            "Omnivore", "Vegetarian", "Vegan", "Pescatarian", "Keto", "Paleo"
        );
        dietaryPreferenceCombo.setItems(dietaryPrefs);
        dietaryPreferenceCombo.setValue("Omnivore");

        // Activity Level options
        ObservableList<String> activityLevels = FXCollections.observableArrayList(
            "Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extremely Active"
        );
        activityLevelCombo.setItems(activityLevels);
        activityLevelCombo.setValue("Moderately Active");

        // Load clients
        loadClients();
    }

    private void loadClients() {
        try {
            var clientNames = dietGeneratorService.getClientsForSelection();
            ObservableList<String> clients = FXCollections.observableArrayList(clientNames);
            clientCombo.setItems(clients);
            if (!clients.isEmpty()) {
                clientCombo.setValue(clients.get(0));
            }
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }

    private void setupSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3);
        mealsPerDaySpinner.setValueFactory(valueFactory);
    }

    @FXML
    private void handleGenerateDiet() {
        if (clientCombo.getValue() == null) {
            showWarning("Please select a client");
            return;
        }

        try {
            progressBar.setVisible(true);
            statusLabel.setVisible(true);
            statusLabel.setText("Generating diet...");
            generateButton.setDisable(true);

            // TODO: build DietRequestDto with AiDto and ClientRequestDto
            DietRequestDto request = new DietRequestDto();

            // Call service asynchronously
            Thread thread = new Thread(() -> {
                try {
                    var result = dietGeneratorService.generateDiet(request);
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("Diet generated successfully!");
                        showInfo("Diet has been generated and saved");
                        clearForm();
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        showError("Failed to generate diet: " + e.getMessage());
                    });
                } finally {
                    javafx.application.Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        generateButton.setDisable(false);
                    });
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            progressBar.setVisible(false);
            generateButton.setDisable(false);
        }
    }

    @FXML
    private void handleBack() {
        // TODO: Navigate back to dashboard
    }

    private void clearForm() {
        notesArea.clear();
        mealsPerDaySpinner.getValueFactory().setValue(3);
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

