package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.service.I18nService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

@Component
public class ClientFormDialog {

    @Autowired
    private I18nService i18n;

    public Optional<ClientDto> showCreate() {
        return show(i18n.t("client.form.title.new"), null);
    }

    public Optional<ClientDto> showEdit(ClientDto existing) {
        return show(i18n.t("client.form.title.edit"), existing);
    }

    private Optional<ClientDto> show(String title, ClientDto existing) {
        Dialog<ClientDto> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null
                ? i18n.t("client.form.header.new")
                : i18n.t("client.form.header.edit"));

        ButtonType saveBtn   = new ButtonType(i18n.t("client.form.btn.save"),   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(i18n.t("client.form.btn.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        TextField nameField    = new TextField(existing != null ? existing.getName()    : "");
        TextField surnameField = new TextField(existing != null ? existing.getSurname() : "");
        TextField ageField     = new TextField(existing != null && existing.getAge() != null
                ? existing.getAge().toString() : "");
        TextField countryField = new TextField(existing != null ? existing.getCountry() : "");

        nameField.setPromptText("es. Mario");
        surnameField.setPromptText("es. Rossi");
        ageField.setPromptText(i18n.t("client.form.age.prompt"));
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

        grid.add(label(i18n.t("client.form.label.name")),    0, 0); grid.add(nameField,    1, 0);
        grid.add(label(i18n.t("client.form.label.surname")), 0, 1); grid.add(surnameField, 1, 1);
        grid.add(label(i18n.t("client.form.label.age")),     0, 2); grid.add(ageField,     1, 2);
        grid.add(label(i18n.t("client.form.label.country")), 0, 3); grid.add(countryField, 1, 3);

        Label requiredNote = new Label(i18n.t("client.form.required"));
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

    public static Stage showForDemo(String name, String surname, int age, String country) {
        Stage stage = new Stage();
        stage.setTitle("Nuovo Cliente");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(16, 24, 8, 24));

        TextField nameField    = new TextField(name);
        TextField surnameField = new TextField(surname);
        TextField ageField     = new TextField(String.valueOf(age));
        TextField countryField = new TextField(country);
        for (TextField tf : new TextField[]{nameField, surnameField, ageField, countryField})
            tf.setPrefWidth(260);

        grid.add(label("Nome *"),    0, 0); grid.add(nameField,    1, 0);
        grid.add(label("Cognome *"), 0, 1); grid.add(surnameField, 1, 1);
        grid.add(label("Eta *"),     0, 2); grid.add(ageField,     1, 2);
        grid.add(label("Paese *"),   0, 3); grid.add(countryField, 1, 3);
        Label note = new Label("* campi obbligatori");
        note.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");
        grid.add(note, 0, 4, 2, 1);

        Button saveBtn   = new Button("Salva");
        Button cancelBtn = new Button("Annulla");
        saveBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16 6 16; -fx-background-radius: 4;");
        cancelBtn.setStyle("-fx-padding: 6 12 6 12; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(8, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(14, 0, 4, 0));

        Label header = new Label("Inserisci i dati del nuovo paziente");
        header.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        VBox root = new VBox(6, header, grid, buttons);
        root.setPadding(new Insets(16, 0, 16, 0));
        root.setStyle("-fx-background-color: white;");

        stage.setScene(new Scene(root, 440, 290));
        stage.setResizable(false);
        stage.show();
        return stage;
    }

    private static Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        return l;
    }
}
