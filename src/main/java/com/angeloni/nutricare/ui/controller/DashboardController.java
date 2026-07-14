package com.angeloni.nutricare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import com.angeloni.nutricare.ui.StageManager;

/**
 * Main dashboard controller for JavaFX
 */
@Controller
public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox sidebar;

    @FXML
    private Button clientsButton;

    @FXML
    private Button dietButton;

    @FXML
    private Button dietGeneratorButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label userLabel;

    private final StageManager stageManager;

    public DashboardController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void initialize() {
        setupUI();
        setupNavigation();
    }

    private void setupUI() {
        // Sidebar styling
        sidebar.setStyle("""
            -fx-background-color: #2c3e50;
            -fx-padding: 20;
            -fx-spacing: 15;
        """);

        // Button styling
        String buttonStyle = """
            -fx-font-size: 13;
            -fx-padding: 12;
            -fx-background-color: #34495e;
            -fx-text-fill: white;
            -fx-cursor: hand;
            -fx-border-radius: 5;
        """;

        clientsButton.setStyle(buttonStyle);
        dietButton.setStyle(buttonStyle);
        dietGeneratorButton.setStyle(buttonStyle);

        logoutButton.setStyle("""
            -fx-font-size: 11;
            -fx-padding: 8;
            -fx-background-color: #e74c3c;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        userLabel.setText("Welcome, User!");

        // Root pane
        rootPane.setStyle("-fx-background-color: #ecf0f1;");
    }

    private void setupNavigation() {
        clientsButton.setOnAction(e -> navigateTo("client"));
        dietButton.setOnAction(e -> navigateTo("diet"));
        dietGeneratorButton.setOnAction(e -> navigateTo("diet-generator"));
        logoutButton.setOnAction(e -> handleLogout());
    }

    private void navigateTo(String sceneName) {
        try {
            stageManager.switchScene(sceneName);
        } catch (IllegalArgumentException e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Esci");
        alert.setHeaderText("Conferma uscita");
        alert.setContentText("Sei sicuro di voler uscire dall'applicazione?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(String username) {
        userLabel.setText("Welcome, " + username + "!");
    }
}

