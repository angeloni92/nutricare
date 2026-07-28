package com.angeloni.nutricare.ui.dialog;

import java.io.File;

import com.angeloni.nutricare.service.I18nService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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

    public static void show(String dietText, String clientName, String providerName, I18nService i18n) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(i18n.t("diet.result.window.title", clientName));
        stage.setMinWidth(780);
        stage.setMinHeight(580);
        stage.setWidth(980);
        stage.setHeight(780);

        // ─── Format selector bar ─────────────────────────────────────────
        ToggleGroup formatGroup = new ToggleGroup();

        RadioButton pdfRadio = new RadioButton("PDF");
        pdfRadio.setToggleGroup(formatGroup);
        pdfRadio.setSelected(true);
        pdfRadio.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-cursor: hand;");

        RadioButton wordRadio = new RadioButton("Word (.docx)");
        wordRadio.setToggleGroup(formatGroup);
        wordRadio.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1d4ed8; -fx-cursor: hand;");

        Label formatLbl = new Label(i18n.t("diet.result.format.label"));
        formatLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox formatBar = new HBox(16, formatLbl, pdfRadio, wordRadio);
        formatBar.setAlignment(Pos.CENTER_LEFT);
        formatBar.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-padding: 10 28 10 28;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // ─── Document page ───────────────────────────────────────────────
        Label docClientLbl = new Label(clientName);
        docClientLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label docSubLbl = new Label(i18n.t("diet.result.subtitle", providerName));
        docSubLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.85);");

        VBox pageHeader = new VBox(3, docClientLbl, docSubLbl);
        pageHeader.setStyle(buildPageHeaderStyle(true));
        pageHeader.setPadding(new Insets(18, 24, 18, 24));

        TextArea textArea = new TextArea(dietText);
        textArea.setEditable(true);
        textArea.setWrapText(true);
        textArea.setStyle(
            "-fx-font-family: 'Georgia', 'Times New Roman', serif;" +
            "-fx-font-size: 13px;" +
            "-fx-control-inner-background: white;" +
            "-fx-background-color: white;" +
            "-fx-border-width: 0;" +
            "-fx-padding: 20 28 20 28;"
        );
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox pageCard = new VBox(pageHeader, textArea);
        pageCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 18, 0, 0, 4);" +
            "-fx-background-radius: 4;"
        );
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox viewerWrapper = new VBox(pageCard);
        viewerWrapper.setAlignment(Pos.TOP_CENTER);
        viewerWrapper.setStyle("-fx-background-color: #64748b; -fx-padding: 28 40 28 40;");
        VBox.setVgrow(pageCard, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(viewerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #64748b; -fx-background-color: #64748b;");

        formatGroup.selectedToggleProperty().addListener((obs, oldT, newT) ->
            pageHeader.setStyle(buildPageHeaderStyle(isPdf(formatGroup))));

        // ─── Top bar ─────────────────────────────────────────────────────
        Label titleLbl = new Label(i18n.t("diet.result.header"));
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label editHint = new Label(i18n.t("diet.result.edit.hint"));
        editHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #6366f1; -fx-font-style: italic;");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(titleLbl, editHint, topSpacer);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 16 28 0 28;");

        VBox topSection = new VBox(topBar, formatBar);

        // ─── Footer ──────────────────────────────────────────────────────
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 14 28 14 28;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1 0 0 0;"
        );

        Button saveBtn = new Button(i18n.t("diet.result.btn.save"));
        saveBtn.setStyle(
            "-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 13px;" +
            "-fx-font-weight: bold; -fx-padding: 9 22 9 22; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> handleSave(stage, textArea, clientName, formatGroup, i18n));

        Button closeBtn = new Button(i18n.t("diet.result.btn.close"));
        closeBtn.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-size: 13px;" +
            "-fx-padding: 9 18 9 18; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> stage.close());

        footer.getChildren().addAll(saveBtn, closeBtn);

        // ─── Root ────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setTop(topSection);
        root.setCenter(scrollPane);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        try {
            String css = DietResultDialog.class.getResource("/styles/nutricare.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.show();
    }

    private static String buildPageHeaderStyle(boolean isPdf) {
        String color = isPdf ? "#dc2626" : "#1d4ed8";
        return "-fx-background-color: " + color + ";";
    }

    private static boolean isPdf(ToggleGroup group) {
        Toggle sel = group.getSelectedToggle();
        if (sel instanceof RadioButton rb) return "PDF".equals(rb.getText());
        return true;
    }

    private static void handleSave(Window owner, TextArea textArea, String clientName,
                                   ToggleGroup formatGroup, I18nService i18n) {
        String text = textArea.getText();
        FileChooser chooser = new FileChooser();
        if (isPdf(formatGroup)) {
            chooser.setTitle(i18n.t("diet.result.chooser.pdf.title"));
            chooser.setInitialFileName(i18n.t("diet.result.filename.prefix") + ExportUtils.safeFilename(clientName) + ".pdf");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            File file = chooser.showSaveDialog(owner);
            if (file == null) return;
            try {
                ExportUtils.writePdf(file, clientName, text);
                showInfo(i18n.t("diet.result.saved.pdf", file.getAbsolutePath()), i18n);
            } catch (Exception e) {
                showError(i18n.t("diet.result.error.pdf", e.getMessage()), i18n);
            }
        } else {
            chooser.setTitle(i18n.t("diet.result.chooser.word.title"));
            chooser.setInitialFileName(i18n.t("diet.result.filename.prefix") + ExportUtils.safeFilename(clientName) + ".docx");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"));
            File file = chooser.showSaveDialog(owner);
            if (file == null) return;
            try {
                ExportUtils.writeDocx(file, clientName, text);
                showInfo(i18n.t("diet.result.saved.word", file.getAbsolutePath()), i18n);
            } catch (Exception e) {
                showError(i18n.t("diet.result.error.word", e.getMessage()), i18n);
            }
        }
    }

    private static void showInfo(String msg, I18nService i18n) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(i18n.t("diet.result.saved.title"));
        a.showAndWait();
    }

    private static void showError(String msg, I18nService i18n) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(i18n.t("common.error.title"));
        a.showAndWait();
    }
}
