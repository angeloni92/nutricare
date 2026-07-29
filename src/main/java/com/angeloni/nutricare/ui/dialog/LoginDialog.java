package com.angeloni.nutricare.ui.dialog;

import com.angeloni.nutricare.exception.AuthException;
import com.angeloni.nutricare.service.AuthService;
import com.angeloni.nutricare.service.UserContextService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Standalone login stage — not a Spring bean.
 * Instantiated by ApplicationInitializer when getCurrentUser() == null.
 * Call showAndWait(); then check userContextService.getCurrentUser() != null.
 */
public class LoginDialog {

    private final AuthService authService;
    private final UserContextService userContextService;
    private final Stage stage;

    public LoginDialog(AuthService authService, UserContextService userContextService) {
        this.authService = authService;
        this.userContextService = userContextService;
        this.stage = buildStage();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    /** Builds a visual-only login stage pre-filled for demo capture (no auth logic). */
    public static Stage buildForCapture() {
        Stage s = new Stage();
        s.setTitle("NutriCare — Accedi");
        s.initStyle(StageStyle.DECORATED);
        s.setResizable(false);

        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER);
        logoRow.setPadding(new Insets(24, 0, 16, 0));
        try {
            Image img = new Image(LoginDialog.class.getResourceAsStream("/images/logo-64.png"));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(38); iv.setFitHeight(38);
            logoRow.getChildren().add(iv);
        } catch (Exception ignored) {}
        VBox logoText = new VBox(2);
        logoText.setAlignment(Pos.CENTER_LEFT);
        Label appName = new Label("NutriCare");
        appName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label appSub = new Label("Nutrition Management");
        appSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        logoText.getChildren().addAll(appName, appSub);
        logoRow.getChildren().add(logoText);

        String fieldStyle = "-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; " +
                "-fx-border-color: #d1d5db; -fx-padding: 7 10 7 10;";
        String labelStyle = "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #374151;";

        VBox form = new VBox(10);
        form.setPadding(new Insets(0, 32, 0, 32));
        Label userLabel = new Label("Username");
        userLabel.setStyle(labelStyle);
        TextField userField = new TextField("demo");
        userField.setStyle(fieldStyle);
        Label passLabel = new Label("Password");
        passLabel.setStyle(labelStyle);
        PasswordField passField = new PasswordField();
        passField.setText("demo123");
        passField.setStyle(fieldStyle);
        form.getChildren().addAll(userLabel, userField, passLabel, passField);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(16, 32, 24, 32));
        Button registerBtn = new Button("Registrati");
        registerBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 7 16 7 16; -fx-background-radius: 6; -fx-cursor: hand;");
        Button loginBtn = new Button("Accedi");
        loginBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 20 7 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnRow.getChildren().addAll(registerBtn, loginBtn);

        VBox root = new VBox(logoRow, form, btnRow);
        root.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(root, 420, 320);
        try {
            String css = LoginDialog.class.getResource("/styles/nutricare.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        s.setScene(scene);
        return s;
    }

    private Stage buildStage() {
        Stage s = new Stage();
        s.setTitle("NutriCare — Accedi");
        s.initStyle(StageStyle.DECORATED);
        s.setResizable(false);

        // ── Logo area ──────────────────────────────────────────────────
        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER);
        logoRow.setPadding(new Insets(24, 0, 16, 0));

        try {
            Image img = new Image(getClass().getResourceAsStream("/images/logo-64.png"));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(38);
            iv.setFitHeight(38);
            logoRow.getChildren().add(iv);
        } catch (Exception ignored) {}

        VBox logoText = new VBox(2);
        logoText.setAlignment(Pos.CENTER_LEFT);
        Label appName = new Label("NutriCare");
        appName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label appSub  = new Label("Nutrition Management");
        appSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        logoText.getChildren().addAll(appName, appSub);
        logoRow.getChildren().add(logoText);

        // ── Form ───────────────────────────────────────────────────────
        VBox form = new VBox(10);
        form.setPadding(new Insets(0, 32, 0, 32));

        Label userLabel = formLabel("Username");
        TextField userField = new TextField();
        userField.setPromptText("Inserisci username");
        userField.setStyle(fieldStyle());

        Label passLabel = formLabel("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Inserisci password");
        passField.setStyle(fieldStyle());

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        form.getChildren().addAll(userLabel, userField, passLabel, passField, errorLabel);

        // ── Buttons ────────────────────────────────────────────────────
        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(16, 32, 24, 32));

        Button registerBtn = new Button("Registrati");
        registerBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 7 16 7 16; -fx-background-radius: 6; -fx-cursor: hand;");

        Button loginBtn = new Button("Accedi");
        loginBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 20 7 20; -fx-background-radius: 6; -fx-cursor: hand;");
        loginBtn.setDefaultButton(true);

        btnRow.getChildren().addAll(registerBtn, loginBtn);

        // ── Actions ────────────────────────────────────────────────────
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Inserisci username e password.");
                return;
            }
            try {
                authService.login(username, password);
                s.close();
            } catch (AuthException ex) {
                showError(errorLabel, ex.getMessage());
                passField.clear();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        userField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doLogin.run(); });
        passField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doLogin.run(); });

        registerBtn.setOnAction(e -> {
            RegisterDialog reg = new RegisterDialog(authService);
            reg.showAndWait();
            if (reg.isRegistered()) s.close();
        });

        // Closing the login window exits the application
        s.setOnCloseRequest(e -> javafx.application.Platform.exit());

        // ── Layout ─────────────────────────────────────────────────────
        VBox root = new VBox(logoRow, form, btnRow);
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root, 420, 320);
        try {
            String css = getClass().getResource("/styles/nutricare.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        s.setScene(scene);
        return s;
    }

    private void showError(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #374151;");
        return l;
    }

    private String fieldStyle() {
        return "-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; " +
               "-fx-border-color: #d1d5db; -fx-padding: 7 10 7 10;";
    }
}
