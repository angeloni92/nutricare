package com.angeloni.nutricare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.springframework.stereotype.Controller;
import com.angeloni.nutricare.ui.service.UiAuthService;

/**
 * Login screen controller for JavaFX
 */
@Controller
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    private final UiAuthService authService;

    public LoginController(UiAuthService authService) {
        this.authService = authService;
    }

    @FXML
    public void initialize() {
        setupUI();
    }

    private void setupUI() {
        loginButton.setStyle("""
            -fx-font-size: 14;
            -fx-padding: 10;
            -fx-background-color: #007bff;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        registerButton.setStyle("""
            -fx-font-size: 14;
            -fx-padding: 10;
            -fx-background-color: #28a745;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password");
            return;
        }

        try {
            // Call authentication service
            var result = authService.login(email, password);
            if (result != null) {
                // Navigate to dashboard
                errorLabel.setVisible(false);
                // TODO: Trigger scene switch to dashboard
            } else {
                showError("Login failed. Please check your credentials.");
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        // TODO: Navigate to register scene
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearForm() {
        emailField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }
}

