package com.angeloni.nutricare.ui.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DietResultDialog {

    public static void show(String dietText, String clientName, String providerName) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Piano Nutrizionale — " + clientName);
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setWidth(980);
        stage.setHeight(760);

        // ─── Header ──────────────────────────────────────────────────────
        HBox header = new HBox(16);
        header.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 20 28 20 28;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 4, 0, 0, 2);"
        );
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerBlock = new VBox(3);
        Label titleLbl = new Label("Piano Nutrizionale");
        titleLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label subLbl = new Label("Cliente: " + clientName + "   |   Generato con: " + providerName);
        subLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        headerBlock.getChildren().addAll(titleLbl, subLbl);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label editHint = new Label("Puoi modificare il testo direttamente");
        editHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #6366f1; -fx-font-style: italic;");

        header.getChildren().addAll(headerBlock, headerSpacer, editHint);

        // ─── Content (editable TextArea) ─────────────────────────────────
        TextArea textArea = new TextArea(dietText);
        textArea.setEditable(true);
        textArea.setWrapText(true);
        textArea.setStyle(
            "-fx-font-family: 'Segoe UI', 'Arial', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-control-inner-background: #ffffff;" +
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;"
        );
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox contentWrapper = new VBox(textArea);
        contentWrapper.setStyle("-fx-background-color: #f8fafc; -fx-padding: 20 24 12 24;");
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);

        // ─── Format selector ─────────────────────────────────────────────
        HBox formatBox = new HBox(20);
        formatBox.setAlignment(Pos.CENTER_LEFT);
        formatBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 0 24 12 24;");

        Label formatLbl = new Label("Formato export:");
        formatLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        ToggleGroup formatGroup = new ToggleGroup();

        RadioButton pdfRadio = new RadioButton("PDF");
        pdfRadio.setToggleGroup(formatGroup);
        pdfRadio.setSelected(true);
        pdfRadio.setStyle("-fx-font-size: 13px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");

        RadioButton wordRadio = new RadioButton("Word (.docx)");
        wordRadio.setToggleGroup(formatGroup);
        wordRadio.setStyle("-fx-font-size: 13px; -fx-text-fill: #1d4ed8; -fx-font-weight: bold;");

        formatBox.getChildren().addAll(formatLbl, pdfRadio, wordRadio);

        // ─── Footer ──────────────────────────────────────────────────────
        HBox footer = new HBox(12);
        footer.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 14 28 14 28;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1 0 0 0;"
        );
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button previewBtn = new Button("  Anteprima");
        previewBtn.setStyle(
            "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 13px;" +
            "-fx-font-weight: bold; -fx-padding: 9 18 9 18; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        previewBtn.setOnAction(e -> handlePreview(stage, textArea, clientName, formatGroup));

        Button saveBtn = new Button("  Salva");
        saveBtn.setStyle(
            "-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 13px;" +
            "-fx-font-weight: bold; -fx-padding: 9 18 9 18; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> handleSave(stage, textArea, clientName, formatGroup));

        Button closeBtn = new Button("Chiudi");
        closeBtn.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-size: 13px;" +
            "-fx-padding: 9 18 9 18; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> stage.close());

        footer.getChildren().addAll(previewBtn, saveBtn, closeBtn);

        // ─── Root ────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setTop(header);

        VBox centerBox = new VBox(contentWrapper, formatBox);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
        BorderPane.setMargin(centerBox, Insets.EMPTY);
        root.setCenter(centerBox);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        try {
            String css = DietResultDialog.class.getResource("/styles/nutricare.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.show();
    }

    private static boolean isPdf(ToggleGroup group) {
        Toggle sel = group.getSelectedToggle();
        if (sel instanceof RadioButton rb) return "PDF".equals(rb.getText());
        return true;
    }

    private static void handlePreview(Window owner, TextArea textArea, String clientName, ToggleGroup formatGroup) {
        try {
            String text = textArea.getText();
            File tmpDir = new File(System.getProperty("java.io.tmpdir"), "nutricare_preview");
            tmpDir.mkdirs();
            File tmpFile;
            if (isPdf(formatGroup)) {
                tmpFile = new File(tmpDir, "preview_" + ExportUtils.safeFilename(clientName) + ".pdf");
                ExportUtils.writePdf(tmpFile, clientName, text);
            } else {
                tmpFile = new File(tmpDir, "preview_" + ExportUtils.safeFilename(clientName) + ".docx");
                ExportUtils.writeDocx(tmpFile, clientName, text);
            }
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(tmpFile);
            } else {
                showInfo("File generato in:\n" + tmpFile.getAbsolutePath());
            }
        } catch (IOException e) {
            showError("Errore anteprima: " + e.getMessage());
        }
    }

    private static void handleSave(Window owner, TextArea textArea, String clientName, ToggleGroup formatGroup) {
        String text = textArea.getText();
        FileChooser chooser = new FileChooser();
        if (isPdf(formatGroup)) {
            chooser.setTitle("Salva come PDF");
            chooser.setInitialFileName("piano_nutrizionale_" + ExportUtils.safeFilename(clientName) + ".pdf");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            File file = chooser.showSaveDialog(owner);
            if (file == null) return;
            try {
                ExportUtils.writePdf(file, clientName, text);
                showInfo("PDF salvato:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showError("Errore PDF: " + e.getMessage());
            }
        } else {
            chooser.setTitle("Salva come Word");
            chooser.setInitialFileName("piano_nutrizionale_" + ExportUtils.safeFilename(clientName) + ".docx");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"));
            File file = chooser.showSaveDialog(owner);
            if (file == null) return;
            try {
                ExportUtils.writeDocx(file, clientName, text);
                showInfo("File Word salvato:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showError("Errore Word: " + e.getMessage());
            }
        }
    }

    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle("Info"); a.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Errore"); a.showAndWait();
    }
}
