package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.FoldDto;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AnthropometryFormDialog {

    public static Optional<AnthropometryDto> show(ClientDto client) {
        Dialog<AnthropometryDto> dialog = new Dialog<>();
        dialog.setTitle("Nuova Visita");
        dialog.setHeaderText("Misurazioni per " + client.getName() + " " + client.getSurname());

        ButtonType saveBtn   = new ButtonType("Salva Visita", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Annulla",      ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        // ── Tab 1: Dati Base ─────────────────────────────────────────────
        TextField heightField = numField("es. 175.0");
        TextField weightField = numField("es. 72.5");

        GridPane baseGrid = new GridPane();
        baseGrid.setHgap(12); baseGrid.setVgap(10);
        baseGrid.setPadding(new Insets(14, 0, 0, 0));
        baseGrid.add(boldLabel("Altezza (cm) *"), 0, 0); baseGrid.add(heightField, 1, 0);
        baseGrid.add(boldLabel("Peso (kg) *"),    0, 1); baseGrid.add(weightField, 1, 1);
        baseGrid.add(new Label("* campi obbligatori"), 0, 2, 2, 1);

        Tab baseTab = new Tab("Dati Base", baseGrid);
        baseTab.setClosable(false);

        // ── Tab 2: Pliche ────────────────────────────────────────────────
        TextField fPettoral  = numField("mm"); TextField fAxillary  = numField("mm");
        TextField fSupra     = numField("mm"); TextField fAbdominal = numField("mm");
        TextField fTriceps   = numField("mm"); TextField fSubscap   = numField("mm");
        TextField fThigh     = numField("mm");

        Label foldNote = new Label("Se compili una plica, compilale tutte.");
        foldNote.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");

        GridPane foldGrid = new GridPane();
        foldGrid.setHgap(12); foldGrid.setVgap(10);
        foldGrid.setPadding(new Insets(14, 0, 0, 0));
        foldGrid.add(boldLabel("Pettorale (mm)"),      0, 0); foldGrid.add(fPettoral,  1, 0);
        foldGrid.add(boldLabel("Ascellare (mm)"),      0, 1); foldGrid.add(fAxillary,  1, 1);
        foldGrid.add(boldLabel("Sopra-iliaca (mm)"),   0, 2); foldGrid.add(fSupra,     1, 2);
        foldGrid.add(boldLabel("Addominale (mm)"),     0, 3); foldGrid.add(fAbdominal, 1, 3);
        foldGrid.add(boldLabel("Tricipite (mm)"),      0, 4); foldGrid.add(fTriceps,   1, 4);
        foldGrid.add(boldLabel("Sottoscapolare (mm)"), 0, 5); foldGrid.add(fSubscap,   1, 5);
        foldGrid.add(boldLabel("Coscia (mm)"),         0, 6); foldGrid.add(fThigh,     1, 6);
        foldGrid.add(foldNote, 0, 7, 2, 1);

        Tab foldTab = new Tab("Pliche Cutanee", new VBox(foldGrid));
        foldTab.setClosable(false);

        // ── Tab 3: Circonferenze ─────────────────────────────────────────
        TextField cChest = numField("cm"); TextField cArm   = numField("cm");
        TextField cWaist = numField("cm"); TextField cHip   = numField("cm");
        TextField cThigh = numField("cm");

        Label circNote = new Label("Se compili una circonferenza, compilale tutte.");
        circNote.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");

        GridPane circGrid = new GridPane();
        circGrid.setHgap(12); circGrid.setVgap(10);
        circGrid.setPadding(new Insets(14, 0, 0, 0));
        circGrid.add(boldLabel("Petto (cm)"),   0, 0); circGrid.add(cChest, 1, 0);
        circGrid.add(boldLabel("Braccio (cm)"), 0, 1); circGrid.add(cArm,   1, 1);
        circGrid.add(boldLabel("Vita (cm)"),    0, 2); circGrid.add(cWaist, 1, 2);
        circGrid.add(boldLabel("Fianchi (cm)"), 0, 3); circGrid.add(cHip,   1, 3);
        circGrid.add(boldLabel("Coscia (cm)"),  0, 4); circGrid.add(cThigh, 1, 4);
        circGrid.add(circNote, 0, 5, 2, 1);

        Tab circTab = new Tab("Circonferenze", new VBox(circGrid));
        circTab.setClosable(false);

        TabPane tabPane = new TabPane(baseTab, foldTab, circTab);
        tabPane.setTabMinWidth(120);
        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().setPrefWidth(480);

        // ── Validation ───────────────────────────────────────────────────
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);

        TextField[] foldFields = {fPettoral, fAxillary, fSupra, fAbdominal, fTriceps, fSubscap, fThigh};
        TextField[] circFields = {cChest, cArm, cWaist, cHip, cThigh};

        Runnable validate = () -> {
            boolean baseOk  = isPositiveDouble(heightField.getText()) && isPositiveDouble(weightField.getText());
            long foldFilled = countFilled(foldFields);
            long circFilled = countFilled(circFields);
            boolean foldOk  = foldFilled == 0 || foldFilled == foldFields.length;
            boolean circOk  = circFilled == 0 || circFilled == circFields.length;
            saveButton.setDisable(!baseOk || !foldOk || !circOk);
        };
        validate.run();

        heightField.textProperty().addListener((o, a, b) -> validate.run());
        weightField.textProperty().addListener((o, a, b) -> validate.run());
        for (TextField tf : foldFields) tf.textProperty().addListener((o, a, b) -> validate.run());
        for (TextField tf : circFields) tf.textProperty().addListener((o, a, b) -> validate.run());

        // ── Result converter ─────────────────────────────────────────────
        dialog.setResultConverter(bt -> {
            if (bt != saveBtn) return null;
            AnthropometryDto dto = new AnthropometryDto();
            dto.setHeight(parseDouble(heightField.getText()));
            dto.setWeight(parseDouble(weightField.getText()));

            if (countFilled(foldFields) == foldFields.length) {
                FoldDto fold = new FoldDto();
                fold.setPectoral(parseDouble(fPettoral.getText()));
                fold.setAxillary(parseDouble(fAxillary.getText()));
                fold.setSuprailiac(parseDouble(fSupra.getText()));
                fold.setAbdominal(parseDouble(fAbdominal.getText()));
                fold.setTriceps(parseDouble(fTriceps.getText()));
                fold.setSubscapolaris(parseDouble(fSubscap.getText()));
                fold.setThigh(parseDouble(fThigh.getText()));
                dto.setFold(fold);
            }

            if (countFilled(circFields) == circFields.length) {
                CircumferenceDto circ = new CircumferenceDto();
                circ.setChest(parseDouble(cChest.getText()));
                circ.setArm(parseDouble(cArm.getText()));
                circ.setWaist(parseDouble(cWaist.getText()));
                circ.setHip(parseDouble(cHip.getText()));
                circ.setThigh(parseDouble(cThigh.getText()));
                dto.setCircumference(circ);
            }
            return dto;
        });

        return dialog.showAndWait();
    }

    private static TextField numField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(200);
        tf.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("[0-9]*\\.?[0-9]*")) tf.setText(n.replaceAll("[^0-9.]", ""));
        });
        return tf;
    }

    private static Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        return l;
    }

    private static boolean isPositiveDouble(String s) {
        if (s == null || s.isBlank()) return false;
        try { return Double.parseDouble(s) > 0; } catch (NumberFormatException e) { return false; }
    }

    private static long countFilled(TextField[] fields) {
        long count = 0;
        for (TextField tf : fields) if (!tf.getText().isBlank()) count++;
        return count;
    }

    private static Double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }
}
