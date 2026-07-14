package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;

import com.angeloni.nutricare.dto.ClientDto;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ClientFormDialog {

    public static Optional<ClientDto> showCreate() {
        return show("Nuovo Cliente", null);
    }

    public static Optional<ClientDto> showEdit(ClientDto existing) {
        return show("Modifica Cliente", existing);
    }

    private static Optional<ClientDto> show(String title, ClientDto existing) {
        Dialog<ClientDto> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Inserisci i dati del nuovo paziente" : "Modifica i dati del paziente");

        ButtonType saveBtn   = new ButtonType("Salva",   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        TextField nameField    = new TextField(existing != null ? existing.getName()    : "");
        TextField surnameField = new TextField(existing != null ? existing.getSurname() : "");
        TextField ageField     = new TextField(existing != null && existing.getAge() != null
                ? existing.getAge().toString() : "");
        TextField countryField = new TextField(existing != null ? existing.getCountry() : "");

        nameField.setPromptText("es. Mario");
        surnameField.setPromptText("es. Rossi");
        ageField.setPromptText("es. 35  (obbligatorio, min 1)");
        countryField.setPromptText("es. Italia");

        for (TextField tf : new TextField[]{nameField, surnameField, ageField, countryField}) {
            tf.setPrefWidth(260);
        }

        ageField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*")) ageField.setText(n.replaceAll("[^\\d]", ""));
        });

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 0, 0, 0));

        grid.add(label("Nome *"),    0, 0); grid.add(nameField,    1, 0);
        grid.add(label("Cognome *"), 0, 1); grid.add(surnameField, 1, 1);
        grid.add(label("Eta *"),     0, 2); grid.add(ageField,     1, 2);
        grid.add(label("Paese *"),   0, 3); grid.add(countryField, 1, 3);

        Label requiredNote = new Label("* campi obbligatori");
        requiredNote.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");
        grid.add(requiredNote, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(420);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        Runnable validate = () -> {
            String ageText = ageField.getText().trim();
            boolean ageOk = !ageText.isEmpty() && ageText.matches("\\d+") && Integer.parseInt(ageText) >= 1;
            saveButton.setDisable(
                    nameField.getText().isBlank() ||
                    surnameField.getText().isBlank() ||
                    countryField.getText().isBlank() ||
                    !ageOk);
        };
        validate.run();
        nameField.textProperty().addListener((obs, o, n) -> validate.run());
        surnameField.textProperty().addListener((obs, o, n) -> validate.run());
        countryField.textProperty().addListener((obs, o, n) -> validate.run());
        ageField.textProperty().addListener((obs, o, n) -> validate.run());

        dialog.setResultConverter(bt -> {
            if (bt != saveBtn) return null;
            ClientDto dto = new ClientDto();
            if (existing != null) dto.setId(existing.getId());
            dto.setName(nameField.getText().trim());
            dto.setSurname(surnameField.getText().trim());
            dto.setCountry(countryField.getText().trim());
            dto.setAge(Integer.parseInt(ageField.getText().trim()));
            return dto;
        });

        return dialog.showAndWait();
    }

    private static Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        return l;
    }
}