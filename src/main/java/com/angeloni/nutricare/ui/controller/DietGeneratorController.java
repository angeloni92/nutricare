package com.angeloni.nutricare.ui.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.CopilotDeviceCodeDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.service.AiUserService;
import com.angeloni.nutricare.service.CopilotAuthService;
import com.angeloni.nutricare.service.CopilotDeviceFlowService;
import com.angeloni.nutricare.service.DietGeneratorService;
import com.angeloni.nutricare.ui.dialog.AiApiKeyDialog;
import com.angeloni.nutricare.ui.dialog.CopilotDeviceFlowDialog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

@Controller
public class DietGeneratorController {

    @Autowired
    private DietGeneratorService dietGeneratorService;

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private CopilotAuthService copilotAuthService;

    @Autowired
    private CopilotDeviceFlowService copilotDeviceFlowService;

    private record AiModelConfig(AINameEnum name, AIModelEnum model) {}

    private static final Map<String, AiModelConfig> AI_MODELS = new LinkedHashMap<>();
    static {
        AI_MODELS.put("ChatGPT GPT-4o",        new AiModelConfig(AINameEnum.CHATGPT, AIModelEnum.GPT4O));
        AI_MODELS.put("ChatGPT GPT-3.5 Turbo", new AiModelConfig(AINameEnum.CHATGPT, AIModelEnum.GPT3TURBO));
        AI_MODELS.put("ChatGPT o1",             new AiModelConfig(AINameEnum.CHATGPT, AIModelEnum.OPENAIO1));
        AI_MODELS.put("Claude 3 Sonnet",        new AiModelConfig(AINameEnum.CLAUDE, AIModelEnum.CLAUDE3SONNET));
        AI_MODELS.put("Claude 3.5 Sonnet",      new AiModelConfig(AINameEnum.CLAUDE, AIModelEnum.CLAUDE35SONNET));
        AI_MODELS.put("GitHub Copilot GPT-4o",  new AiModelConfig(AINameEnum.GITHUB_COPILOT, AIModelEnum.COPILOT_GPT4O));
    }

    private ComboBox<String> aiModelCombo;
    private ComboBox<String> clientCombo;
    private Button generateButton;
    private Button configureButton;
    private Label credentialStatusLabel;
    private ProgressIndicator progressIndicator;

    public void setup(ComboBox<String> aiModelCombo, ComboBox<String> clientCombo,
                      Button generateButton, Button configureButton,
                      Label credentialStatusLabel, ProgressIndicator progressIndicator) {
        this.aiModelCombo = aiModelCombo;
        this.clientCombo = clientCombo;
        this.generateButton = generateButton;
        this.configureButton = configureButton;
        this.credentialStatusLabel = credentialStatusLabel;
        this.progressIndicator = progressIndicator;

        aiModelCombo.setItems(FXCollections.observableArrayList(AI_MODELS.keySet()));
        aiModelCombo.setValue("ChatGPT GPT-4o");

        loadClients();
        updateCredentialStatus();

        aiModelCombo.setOnAction(e -> updateCredentialStatus());
        generateButton.setOnAction(e -> handleGenerateDiet());
        configureButton.setOnAction(e -> handleConfigureCredentials());
    }

    private void loadClients() {
        try {
            var names = dietGeneratorService.getClientsForSelection();
            clientCombo.setItems(FXCollections.observableArrayList(names));
            if (!names.isEmpty()) {
                clientCombo.setValue(names.get(0));
            }
        } catch (Exception e) {
            showError("Impossibile caricare i clienti: " + e.getMessage());
        }
    }

    private void updateCredentialStatus() {
        String selected = aiModelCombo.getValue();
        if (selected == null) return;
        AiModelConfig cfg = AI_MODELS.get(selected);
        if (cfg == null) return;

        boolean configured = checkCredentialsConfigured(cfg);
        if (configured) {
            credentialStatusLabel.setText("Credenziali configurate");
            credentialStatusLabel.setStyle("-fx-text-fill: #198754; -fx-font-size: 11;");
        } else {
            credentialStatusLabel.setText("Credenziali non configurate — clicca 'Configura'");
            credentialStatusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 11;");
        }
    }

    private boolean checkCredentialsConfigured(AiModelConfig cfg) {
        if (cfg.name() == AINameEnum.GITHUB_COPILOT) {
            return Boolean.TRUE.equals(copilotAuthService.getCurrentConnectionStatus().getConnected());
        }
        return aiUserService.hasApiKey(cfg.name(), cfg.model());
    }

    public void handleConfigureCredentials() {
        String selected = aiModelCombo.getValue();
        if (selected == null) return;
        AiModelConfig cfg = AI_MODELS.get(selected);
        if (cfg == null) return;

        if (cfg.name() == AINameEnum.GITHUB_COPILOT) {
            configureGithubCopilot();
        } else {
            configureApiKey(selected, cfg);
        }
        updateCredentialStatus();
    }

    private void configureApiKey(String displayName, AiModelConfig cfg) {
        AiApiKeyDialog.show(displayName).ifPresent(key -> {
            try {
                aiUserService.saveApiKey(cfg.name(), cfg.model(), key);
                showInfo("API Key salvata con successo per " + displayName);
            } catch (Exception e) {
                showError("Errore nel salvataggio della API Key: " + e.getMessage());
            }
        });
    }

    private void configureGithubCopilot() {
        try {
            CopilotDeviceCodeDto deviceCode = copilotDeviceFlowService.startDeviceFlow();
            boolean authorized = CopilotDeviceFlowDialog.show(deviceCode, copilotDeviceFlowService);
            if (authorized) {
                showInfo("GitHub Copilot connesso con successo!");
            }
        } catch (Exception e) {
            showError("Errore nell'autorizzazione Copilot: " + e.getMessage());
        }
    }

    public void handleGenerateDiet() {
        if (clientCombo.getValue() == null) {
            showWarning("Seleziona un cliente");
            return;
        }
        String selected = aiModelCombo.getValue();
        AiModelConfig cfg = AI_MODELS.get(selected);

        if (!checkCredentialsConfigured(cfg)) {
            showWarning("Le credenziali per " + selected + " non sono configurate.\nClicca 'Configura' per inserirle.");
            return;
        }

        progressIndicator.setVisible(true);
        generateButton.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                DietRequestDto request = new DietRequestDto();
                dietGeneratorService.generateDiet(request);
                Platform.runLater(() -> {
                    showInfo("Dieta generata e salvata con successo!");
                    progressIndicator.setVisible(false);
                    generateButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Errore durante la generazione: " + e.getMessage());
                    progressIndicator.setVisible(false);
                    generateButton.setDisable(false);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Errore");
        a.showAndWait();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setTitle("Attenzione");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle("Successo");
        a.showAndWait();
    }
}