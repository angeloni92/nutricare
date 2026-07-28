package com.angeloni.nutricare.ui.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.service.I18nService;
import com.angeloni.nutricare.service.LicenseService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Component
public class LicenseDialog {

    @Autowired private LicenseService licenseService;
    @Autowired private I18nService i18n;

    /**
     * Shows a blocking license dialog. Returns true if the user successfully
     * activated a license key, false if they chose to exit.
     */
    public boolean show() {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(i18n.t("license.dialog.title"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        // Prevent closing with X
        stage.setOnCloseRequest(e -> e.consume());

        VBox root = new VBox(18);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: white;");
        root.setAlignment(Pos.TOP_LEFT);

        Label header = new Label(i18n.t("license.dialog.header"));
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        // Machine ID section
        Label machineLabel = new Label(i18n.t("license.dialog.machine.label"));
        machineLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        TextField machineIdField = new TextField(licenseService.getMachineId());
        machineIdField.setEditable(false);
        machineIdField.setStyle(
            "-fx-font-family: monospace; -fx-font-size: 12px; -fx-background-color: #f8fafc;" +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-radius: 6;" +
            "-fx-padding: 8 12 8 12;"
        );
        machineIdField.setPrefWidth(500);

        // License key input
        Label keyLabel = new Label(i18n.t("license.dialog.key.label"));
        keyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        TextArea keyArea = new TextArea();
        keyArea.setPromptText(i18n.t("license.dialog.key.prompt"));
        keyArea.setPrefRowCount(4);
        keyArea.setWrapText(true);
        keyArea.setStyle(
            "-fx-font-family: monospace; -fx-font-size: 11px;" +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-radius: 6;"
        );

        Label errorLabel = new Label(i18n.t("license.dialog.error"));
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Buttons
        Button activateBtn = new Button(i18n.t("license.dialog.btn.activate"));
        activateBtn.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 8 20 8 20; -fx-background-radius: 6; -fx-font-size: 13px;"
        );

        Button exitBtn = new Button(i18n.t("license.dialog.btn.exit"));
        exitBtn.setStyle(
            "-fx-background-color: #f1f5f9; -fx-text-fill: #374151;" +
            "-fx-padding: 8 16 8 16; -fx-background-radius: 6; -fx-font-size: 13px;"
        );

        HBox btnRow = new HBox(12, activateBtn, exitBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        boolean[] result = {false};

        activateBtn.setOnAction(e -> {
            String key = keyArea.getText().trim();
            if (licenseService.activate(key)) {
                result[0] = true;
                stage.close();
            } else {
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        exitBtn.setOnAction(e -> {
            result[0] = false;
            stage.close();
        });

        root.getChildren().addAll(header, machineLabel, machineIdField, keyLabel, keyArea, errorLabel, btnRow);

        stage.setScene(new Scene(root, 560, 420));
        stage.showAndWait();
        return result[0];
    }
}
