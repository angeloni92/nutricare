package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.FoldDto;
import com.angeloni.nutricare.service.I18nService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

@Component
public class AnthropometryFormDialog {

    @Autowired
    private I18nService i18n;

    public Optional<AnthropometryDto> show(ClientDto client) {
        Dialog<AnthropometryDto> dialog = new Dialog<>();
        dialog.setTitle(i18n.t("visit.dialog.title"));
        dialog.setHeaderText(i18n.t("visit.dialog.header", client.getName() + " " + client.getSurname()));

        ButtonType saveBtn   = new ButtonType(i18n.t("visit.dialog.btn.save"),   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(i18n.t("visit.dialog.btn.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        // ── Tab 1: Dati Base ─────────────────────────────────────────────
        TextField heightField = numField("es. 175.0");
        TextField weightField = numField("es. 72.5");

        GridPane baseGrid = new GridPane();
        baseGrid.setHgap(12); baseGrid.setVgap(10);
        baseGrid.setPadding(new Insets(14, 0, 0, 0));
        baseGrid.add(boldLabel(i18n.t("visit.field.height")), 0, 0); baseGrid.add(heightField, 1, 0);
        baseGrid.add(boldLabel(i18n.t("visit.field.weight")), 0, 1); baseGrid.add(weightField, 1, 1);
        baseGrid.add(new Label(i18n.t("visit.field.required")), 0, 2, 2, 1);

        Tab baseTab = new Tab(i18n.t("visit.tab.base"), baseGrid);
        baseTab.setClosable(false);

        // ── Tab 2: Pliche ────────────────────────────────────────────────
        TextField fPettoral  = numField("mm"); TextField fAxillary  = numField("mm");
        TextField fSupra     = numField("mm"); TextField fAbdominal = numField("mm");
        TextField fTriceps   = numField("mm"); TextField fSubscap   = numField("mm");
        TextField fThigh     = numField("mm");

        Label foldNote = new Label(i18n.t("visit.fold.note"));
        foldNote.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");

        GridPane foldGrid = new GridPane();
        foldGrid.setHgap(12); foldGrid.setVgap(10);
        foldGrid.setPadding(new Insets(14, 0, 0, 0));
        foldGrid.add(boldLabel(i18n.t("visit.fold.pectoral")),    0, 0); foldGrid.add(fPettoral,  1, 0);
        foldGrid.add(boldLabel(i18n.t("visit.fold.axillary")),    0, 1); foldGrid.add(fAxillary,  1, 1);
        foldGrid.add(boldLabel(i18n.t("visit.fold.suprailiac")),  0, 2); foldGrid.add(fSupra,     1, 2);
        foldGrid.add(boldLabel(i18n.t("visit.fold.abdominal")),   0, 3); foldGrid.add(fAbdominal, 1, 3);
        foldGrid.add(boldLabel(i18n.t("visit.fold.triceps")),     0, 4); foldGrid.add(fTriceps,   1, 4);
        foldGrid.add(boldLabel(i18n.t("visit.fold.subscapular")), 0, 5); foldGrid.add(fSubscap,   1, 5);
        foldGrid.add(boldLabel(i18n.t("visit.fold.thigh")),       0, 6); foldGrid.add(fThigh,     1, 6);
        foldGrid.add(foldNote, 0, 7, 2, 1);

        Tab foldTab = new Tab(i18n.t("visit.tab.folds"), new VBox(foldGrid));
        foldTab.setClosable(false);

        // ── Tab 3: Circonferenze ─────────────────────────────────────────
        TextField cChest = numField("cm"); TextField cArm   = numField("cm");
        TextField cWaist = numField("cm"); TextField cHip   = numField("cm");
        TextField cThigh = numField("cm");

        Label circNote = new Label(i18n.t("visit.circ.note"));
        circNote.setStyle("-fx-font-size: 11; -fx-text-fill: #6c757d;");

        GridPane circGrid = new GridPane();
        circGrid.setHgap(12); circGrid.setVgap(10);
        circGrid.setPadding(new Insets(14, 0, 0, 0));
        circGrid.add(boldLabel(i18n.t("visit.circ.chest")), 0, 0); circGrid.add(cChest, 1, 0);
        circGrid.add(boldLabel(i18n.t("visit.circ.arm")),   0, 1); circGrid.add(cArm,   1, 1);
        circGrid.add(boldLabel(i18n.t("visit.circ.waist")), 0, 2); circGrid.add(cWaist, 1, 2);
        circGrid.add(boldLabel(i18n.t("visit.circ.hip")),   0, 3); circGrid.add(cHip,   1, 3);
        circGrid.add(boldLabel(i18n.t("visit.circ.thigh")), 0, 4); circGrid.add(cThigh, 1, 4);
        circGrid.add(circNote, 0, 5, 2, 1);

        Tab circTab = new Tab(i18n.t("visit.tab.circ"), new VBox(circGrid));
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

    private TextField numField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(200);
        tf.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("[0-9]*\\.?[0-9]*")) tf.setText(n.replaceAll("[^0-9.]", ""));
        });
        return tf;
    }

    private Label boldLabel(String text) {
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

    public Stage showForDemo(String clientFullName, double height, double weight) {
        Stage stage = new Stage();
        stage.setTitle(i18n.t("visit.dialog.title"));

        // ── Tab Dati Base ─────────────────────────────────────────────────
        TextField heightField = filledField(String.valueOf(height));
        TextField weightField = filledField(String.valueOf(weight));

        GridPane baseGrid = new GridPane();
        baseGrid.setHgap(12); baseGrid.setVgap(10);
        baseGrid.setPadding(new Insets(14, 0, 0, 0));
        baseGrid.add(boldLabel(i18n.t("visit.field.height")), 0, 0); baseGrid.add(heightField, 1, 0);
        baseGrid.add(boldLabel(i18n.t("visit.field.weight")), 0, 1); baseGrid.add(weightField, 1, 1);
        baseGrid.add(new Label(i18n.t("visit.field.required")), 0, 2, 2, 1);
        Tab baseTab = new Tab(i18n.t("visit.tab.base"), baseGrid);
        baseTab.setClosable(false);

        // ── Tab Pliche ────────────────────────────────────────────────────
        GridPane foldGrid = new GridPane();
        foldGrid.setHgap(12); foldGrid.setVgap(10);
        foldGrid.setPadding(new Insets(14, 0, 0, 0));
        foldGrid.add(boldLabel(i18n.t("visit.fold.pectoral")),    0, 0); foldGrid.add(filledField("12.0"), 1, 0);
        foldGrid.add(boldLabel(i18n.t("visit.fold.axillary")),    0, 1); foldGrid.add(filledField("10.0"), 1, 1);
        foldGrid.add(boldLabel(i18n.t("visit.fold.suprailiac")),  0, 2); foldGrid.add(filledField("18.0"), 1, 2);
        foldGrid.add(boldLabel(i18n.t("visit.fold.abdominal")),   0, 3); foldGrid.add(filledField("22.0"), 1, 3);
        foldGrid.add(boldLabel(i18n.t("visit.fold.triceps")),     0, 4); foldGrid.add(filledField("15.0"), 1, 4);
        foldGrid.add(boldLabel(i18n.t("visit.fold.subscapular")), 0, 5); foldGrid.add(filledField("14.0"), 1, 5);
        foldGrid.add(boldLabel(i18n.t("visit.fold.thigh")),       0, 6); foldGrid.add(filledField("20.0"), 1, 6);
        Tab foldTab = new Tab(i18n.t("visit.tab.folds"), new VBox(foldGrid));
        foldTab.setClosable(false);

        // ── Tab Circonferenze ─────────────────────────────────────────────
        GridPane circGrid = new GridPane();
        circGrid.setHgap(12); circGrid.setVgap(10);
        circGrid.setPadding(new Insets(14, 0, 0, 0));
        circGrid.add(boldLabel(i18n.t("visit.circ.chest")), 0, 0); circGrid.add(filledField("92.0"), 1, 0);
        circGrid.add(boldLabel(i18n.t("visit.circ.arm")),   0, 1); circGrid.add(filledField("33.0"), 1, 1);
        circGrid.add(boldLabel(i18n.t("visit.circ.waist")), 0, 2); circGrid.add(filledField("78.0"), 1, 2);
        circGrid.add(boldLabel(i18n.t("visit.circ.hip")),   0, 3); circGrid.add(filledField("96.0"), 1, 3);
        circGrid.add(boldLabel(i18n.t("visit.circ.thigh")), 0, 4); circGrid.add(filledField("55.0"), 1, 4);
        Tab circTab = new Tab(i18n.t("visit.tab.circ"), new VBox(circGrid));
        circTab.setClosable(false);

        // ── Layout ───────────────────────────────────────────────────────
        TabPane tabPane = new TabPane(baseTab, foldTab, circTab);
        tabPane.setTabMinWidth(120);

        Button saveBtn   = new Button(i18n.t("visit.dialog.btn.save"));
        Button cancelBtn = new Button(i18n.t("visit.dialog.btn.cancel"));
        saveBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16 6 16; -fx-background-radius: 4;");
        cancelBtn.setStyle("-fx-padding: 6 12 6 12; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(8, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Label header = new Label(i18n.t("visit.dialog.header", clientFullName));
        header.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        VBox root = new VBox(8, header, tabPane, buttons);
        root.setPadding(new Insets(16, 16, 16, 16));
        root.setStyle("-fx-background-color: white;");

        stage.setScene(new Scene(root, 480, 400));
        stage.setResizable(false);
        stage.show();
        return stage;
    }

    private TextField filledField(String value) {
        TextField tf = numField(value);
        tf.setText(value);
        return tf;
    }

    private static Double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }
}
