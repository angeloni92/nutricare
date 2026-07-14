package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AiApiKeyDialog {

    public static Optional<String> show(String aiDisplayName) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Configura API Key");
        dialog.setHeaderText("Inserisci la API Key per " + aiDisplayName);

        ButtonType saveBtn = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Incolla qui la tua API Key...");
        passwordField.setPrefWidth(380);

        TextField visibleField = new TextField();
        visibleField.setPrefWidth(380);
        visibleField.setVisible(false);
        visibleField.setManaged(false);

        passwordField.textProperty().bindBidirectional(visibleField.textProperty());

        CheckBox showKey = new CheckBox("Mostra");
        showKey.setOnAction(e -> {
            boolean show = showKey.isSelected();
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);
            visibleField.setVisible(show);
            visibleField.setManaged(show);
        });

        HBox fieldRow = new HBox(8, passwordField, visibleField, showKey);
        fieldRow.setAlignment(Pos.CENTER_LEFT);

        Label hint = new Label("La chiave viene cifrata con AES-256 prima di essere salvata nel database.");
        hint.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");

        VBox content = new VBox(10,
                new Label("API Key:"),
                fieldRow,
                hint);
        content.setPadding(new Insets(10, 0, 0, 0));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.setDisable(true);
        passwordField.textProperty().addListener((obs, o, n) ->
                saveButton.setDisable(n == null || n.isBlank()));

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                String val = passwordField.isVisible() ? passwordField.getText() : visibleField.getText();
                return val != null ? val.trim() : null;
            }
            return null;
        });

        return dialog.showAndWait().filter(k -> k != null && !k.isBlank());
    }
}