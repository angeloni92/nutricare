package com.angeloni.nutricare.ui.dialog;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.angeloni.nutricare.dto.CopilotDeviceCodeDto;
import com.angeloni.nutricare.service.CopilotDeviceFlowService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CopilotAuthDialog {

    public static Optional<String> show(CopilotDeviceFlowService deviceFlowService) {
        CopilotDeviceCodeDto code;
        try {
            code = deviceFlowService.startDeviceFlow();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Impossibile avviare la procedura di accesso GitHub:\n" + e.getMessage(), ButtonType.OK);
            alert.setTitle("Errore Copilot");
            alert.showAndWait();
            return Optional.empty();
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Connetti GitHub Copilot");
        dialog.setHeaderText("Autorizza NutriCare su GitHub");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(460);

        Label instructions = new Label(
                "1. Clicca \"Apri GitHub\" per aprire la pagina di autorizzazione.\n" +
                "2. Inserisci il codice qui sotto quando richiesto.\n" +
                "3. La finestra si chiuderà automaticamente al completamento.");
        instructions.setWrapText(true);

        Label codeLabel = new Label(code.getUserCode());
        codeLabel.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-font-family: monospace; -fx-text-fill: #0d6efd; -fx-letter-spacing: 4;");

        Button copyBtn = new Button("Copia codice");
        copyBtn.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(code.getUserCode());
            Clipboard.getSystemClipboard().setContent(content);
            copyBtn.setText("Copiato!");
        });

        HBox codeBox = new HBox(12, codeLabel, copyBtn);
        codeBox.setAlignment(Pos.CENTER_LEFT);

        Button openBrowserBtn = new Button("Apri GitHub");
        openBrowserBtn.setStyle("-fx-background-color: #24292e; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 18;");
        openBrowserBtn.setOnAction(e -> openUrl(code.getVerificationUri()));

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(22, 22);
        Label waitLabel = new Label("In attesa di autorizzazione...");
        waitLabel.setStyle("-fx-text-fill: #6c757d;");
        HBox waitBox = new HBox(8, progress, waitLabel);
        waitBox.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(14, instructions, codeBox, openBrowserBtn, waitBox);
        content.setPadding(new Insets(10, 0, 0, 0));
        dialog.getDialogPane().setContent(content);

        dialog.setOnShown(e -> openUrl(code.getVerificationUri()));

        CompletableFuture<String> future = deviceFlowService.pollForToken(code.getDeviceCode(), code.getInterval());

        future.thenAccept(githubLogin -> Platform.runLater(() -> {
            dialog.setResult("OK:" + githubLogin);
            dialog.close();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                progress.setVisible(false);
                waitLabel.setText("Errore: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
                waitLabel.setStyle("-fx-text-fill: #dc3545;");
            });
            return null;
        });

        dialog.setOnCloseRequest(e -> future.cancel(true));

        return dialog.showAndWait().filter(r -> r != null && r.startsWith("OK:"));
    }

    private static void openUrl(String url) {
        try {
            new ProcessBuilder("cmd", "/c", "start", url).start();
        } catch (Exception ex) {
            // fallback per non-Windows
            try {
                new ProcessBuilder("xdg-open", url).start();
            } catch (Exception ignored) {}
        }
    }
}
