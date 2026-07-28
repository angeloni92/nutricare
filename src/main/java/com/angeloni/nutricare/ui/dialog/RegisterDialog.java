package com.angeloni.nutricare.ui.dialog;

import com.angeloni.nutricare.service.AuthService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Standalone registration stage — not a Spring bean.
 * After showAndWait(), check isRegistered() to know if a new account was created.
 */
public class RegisterDialog {

    private final AuthService authService;
    private final Stage stage;
    private boolean registered = false;

    public RegisterDialog(AuthService authService) {
        this.authService = authService;
        this.stage = buildStage();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public boolean isRegistered() {
        return registered;
    }

    private Stage buildStage() {
        Stage s = new Stage();
        s.setTitle("NutriCare — Crea Account");
        s.initStyle(StageStyle.DECORATED);
        s.setResizable(false);

        // ── Header ─────────────────────────────────────────────────────
        Label title = new Label("Crea Account");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        HBox header = new HBox(title);
        header.setPadding(new Insets(20, 24, 8, 24));

        // ── Form grid ──────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 24, 8, 24));

        TextField  userField    = new TextField();
        TextField  emailField   = new TextField();
        PasswordField passField = new PasswordField();
        PasswordField confField = new PasswordField();

        userField.setPromptText("es. mario.rossi");
        emailField.setPromptText("es. mario@email.com");
        passField.setPromptText("minimo 6 caratteri");
        confField.setPromptText("ripeti la password");

        for (var tf : new javafx.scene.control.TextInputControl[]{userField, emailField, passField, confField})
            tf.setStyle(fieldStyle());

        grid.add(formLabel("Username *"),         0, 0); grid.add(userField,  1, 0);
        grid.add(formLabel("Email *"),            0, 1); grid.add(emailField, 1, 1);
        grid.add(formLabel("Password *"),         0, 2); grid.add(passField,  1, 2);
        grid.add(formLabel("Conferma password *"),0, 3); grid.add(confField,  1, 3);

        Label requiredNote = new Label("* campi obbligatori");
        requiredNote.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        grid.add(requiredNote, 0, 4, 2, 1);

        // ── Error label ────────────────────────────────────────────────
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setPadding(new Insets(0, 24, 0, 24));
        errorLabel.setMaxWidth(340);

        // ── Buttons ────────────────────────────────────────────────────
        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10, 24, 20, 24));

        Button cancelBtn = new Button("Annulla");
        cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 7 16 7 16; -fx-background-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> s.close());

        Button createBtn = new Button("Crea Account");
        createBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 20 7 20; -fx-background-radius: 6; -fx-cursor: hand;");
        createBtn.setDefaultButton(true);

        btnRow.getChildren().addAll(cancelBtn, createBtn);

        // ── Validation + action ────────────────────────────────────────
        createBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String email    = emailField.getText().trim();
            String password = passField.getText();
            String confirm  = confField.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showError(errorLabel, "Compila tutti i campi obbligatori.");
                return;
            }
            if (!email.contains("@")) {
                showError(errorLabel, "Inserisci un indirizzo email valido.");
                return;
            }
            if (password.length() < 6) {
                showError(errorLabel, "La password deve contenere almeno 6 caratteri.");
                return;
            }
            if (!password.equals(confirm)) {
                showError(errorLabel, "Le password non coincidono.");
                passField.clear();
                confField.clear();
                return;
            }
            try {
                authService.register(username, password, email);
                registered = true;
                s.close();
            } catch (IllegalArgumentException ex) {
                showError(errorLabel, ex.getMessage());
            }
        });

        // ── Layout ─────────────────────────────────────────────────────
        VBox root = new VBox(header, grid, errorLabel, btnRow);
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root, 380, 360);
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
               "-fx-border-color: #d1d5db; -fx-padding: 7 10 7 10; -fx-pref-width: 210;";
    }
}
