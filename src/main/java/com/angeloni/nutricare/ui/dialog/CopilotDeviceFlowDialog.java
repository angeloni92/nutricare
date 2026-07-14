package com.angeloni.nutricare.ui.dialog;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import com.angeloni.nutricare.dto.CopilotDeviceCodeDto;
import com.angeloni.nutricare.service.CopilotDeviceFlowService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CopilotDeviceFlowDialog {

    public static boolean show(CopilotDeviceCodeDto deviceCode, CopilotDeviceFlowService service) {
        AtomicBoolean authorized = new AtomicBoolean(false);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Connetti GitHub Copilot");
        dialog.setResizable(false);

        Label instruction = new Label(
                "1. Visita il link sottostante\n2. Inserisci il codice mostrato\n3. Autorizza l'applicazione");
        instruction.setStyle("-fx-font-size: 13;");

        Hyperlink urlLink = new Hyperlink(deviceCode.getVerificationUri());
        urlLink.setOnAction(e -> openBrowser(deviceCode.getVerificationUri()));

        Label codeLabel = new Label(deviceCode.getUserCode());
        codeLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-font-family: monospace; "
                + "-fx-background-color: #f0f4f8; -fx-padding: 12 24 12 24; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");

        Button copyBtn = new Button("Copia codice");
        copyBtn.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand;");
        copyBtn.setOnAction(e -> {
            Clipboard cb = Clipboard.getSystemClipboard();
            ClipboardContent cc = new ClipboardContent();
            cc.putString(deviceCode.getUserCode());
            cb.setContent(cc);
            copyBtn.setText("Copiato!");
        });

        Button openBtn = new Button("Apri nel browser");
        openBtn.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand;");
        openBtn.setOnAction(e -> openBrowser(deviceCode.getVerificationUri()));

        HBox buttonRow = new HBox(10, copyBtn, openBtn);
        buttonRow.setAlignment(Pos.CENTER);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(28, 28);

        Label statusLabel = new Label("In attesa di autorizzazione...");
        statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6c757d;");

        HBox statusRow = new HBox(10, spinner, statusLabel);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Button cancelBtn = new Button("Annulla");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(18,
                instruction,
                new Separator(),
                new Label("Apri questo indirizzo nel browser:"),
                urlLink,
                new Label("Inserisci questo codice:"),
                codeLabel,
                buttonRow,
                new Separator(),
                statusRow,
                cancelBtn);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #ffffff;");

        dialog.setScene(new Scene(root, 460, 460));

        CompletableFuture<Void> polling = service.pollForToken(deviceCode.getDeviceCode(), deviceCode.getInterval());

        polling.thenRun(() -> Platform.runLater(() -> {
            authorized.set(true);
            dialog.close();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                statusLabel.setText("Errore: " + cause.getMessage());
                statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #dc3545;");
                spinner.setVisible(false);
            });
            return null;
        });

        dialog.showAndWait();
        return authorized.get();
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignored) {
        }
    }
}
