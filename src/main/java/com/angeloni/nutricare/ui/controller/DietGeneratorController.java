package com.angeloni.nutricare.ui.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.ClientRequestDto;
import com.angeloni.nutricare.dto.DietDetailDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.enums.ActivityLevelEnum;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.enums.DietaryPreferenceEnum;
import com.angeloni.nutricare.enums.PrimaryGoalEnum;
import com.angeloni.nutricare.service.AiUserService;
import com.angeloni.nutricare.service.AnthropometryService;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.service.DietGeneratorService;
import com.angeloni.nutricare.service.I18nService;
import com.angeloni.nutricare.ui.StageManager;
import com.angeloni.nutricare.ui.dialog.AiApiKeyDialog;
import com.angeloni.nutricare.ui.dialog.DietGenerationProgressDialog;
import com.angeloni.nutricare.ui.dialog.DietResultDialog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

@Controller
public class DietGeneratorController {

    @Autowired
    private StageManager stageManager;

    @Autowired
    private DietGeneratorService dietGeneratorService;

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private AnthropometryService anthropometryService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private I18nService i18n;

    private static final Map<String, AINameEnum> PROVIDERS = new LinkedHashMap<>();
    private static final Map<AINameEnum, Map<String, AIModelEnum>> PROVIDER_MODELS = new LinkedHashMap<>();

    static {
        PROVIDERS.put("ChatGPT (OpenAI)", AINameEnum.CHATGPT);
        PROVIDERS.put("Claude (Anthropic)", AINameEnum.CLAUDE);
        PROVIDERS.put("Google Gemini", AINameEnum.GEMINI);

        Map<String, AIModelEnum> chatgptModels = new LinkedHashMap<>();
        chatgptModels.put("GPT-4o  (consigliato)", AIModelEnum.GPT4O);
        chatgptModels.put("GPT-4o mini  (economico)", AIModelEnum.GPT4O_MINI);
        chatgptModels.put("o3  (ragionamento completo)", AIModelEnum.OPENAIO3);
        chatgptModels.put("o3-mini  (ragionamento)", AIModelEnum.OPENAIO3MINI);
        chatgptModels.put("o4-mini  (ragionamento recente)", AIModelEnum.OPENAIO4MINI);
        chatgptModels.put("o1  (ragionamento avanzato)", AIModelEnum.OPENAIO1);
        chatgptModels.put("o1-mini  (ragionamento leggero)", AIModelEnum.OPENAIO1MINI);
        chatgptModels.put("GPT-4 Turbo", AIModelEnum.GPT4TURBO);
        chatgptModels.put("GPT-4", AIModelEnum.GPT4);
        chatgptModels.put("GPT-3.5 Turbo  (legacy)", AIModelEnum.GPT3TURBO);
        PROVIDER_MODELS.put(AINameEnum.CHATGPT, chatgptModels);

        Map<String, AIModelEnum> claudeModels = new LinkedHashMap<>();
        claudeModels.put("Claude Fable 5  (il più potente)", AIModelEnum.CLAUDE5FABLE);
        claudeModels.put("Claude Sonnet 5  (consigliato)", AIModelEnum.CLAUDE5SONNET);
        claudeModels.put("Claude Sonnet 4.6", AIModelEnum.CLAUDE4SONNET);
        claudeModels.put("Claude Opus 4.8  (avanzato)", AIModelEnum.CLAUDE48OPUS);
        claudeModels.put("Claude Opus 4", AIModelEnum.CLAUDE4OPUS);
        claudeModels.put("Claude 3.7 Sonnet", AIModelEnum.CLAUDE37SONNET);
        claudeModels.put("Claude 3.5 Sonnet", AIModelEnum.CLAUDE35SONNET);
        claudeModels.put("Claude 4.5 Haiku  (veloce)", AIModelEnum.CLAUDE45HAIKU);
        claudeModels.put("Claude 3.5 Haiku  (veloce)", AIModelEnum.CLAUDE35HAIKU);
        claudeModels.put("Claude 3 Opus  (legacy)", AIModelEnum.CLAUDE3OPUS);
        claudeModels.put("Claude 3 Sonnet  (legacy)", AIModelEnum.CLAUDE3SONNET);
        claudeModels.put("Claude 3 Haiku  (legacy)", AIModelEnum.CLAUDE3HAIKU);
        PROVIDER_MODELS.put(AINameEnum.CLAUDE, claudeModels);

        Map<String, AIModelEnum> geminiModels = new LinkedHashMap<>();
        geminiModels.put("Gemini 2.5 Pro  (più recente)", AIModelEnum.GEMINI_25_PRO);
        geminiModels.put("Gemini 2.5 Flash  (consigliato)", AIModelEnum.GEMINI_25_FLASH);
        geminiModels.put("Gemini 2.0 Flash", AIModelEnum.GEMINI_20_FLASH);
        geminiModels.put("Gemini 2.0 Flash Lite  (leggero)", AIModelEnum.GEMINI_20_FLASH_LITE);
        geminiModels.put("Gemini 1.5 Flash", AIModelEnum.GEMINI_15_FLASH);
        geminiModels.put("Gemini 1.5 Pro", AIModelEnum.GEMINI_15_PRO);
        PROVIDER_MODELS.put(AINameEnum.GEMINI, geminiModels);
    }

    private ComboBox<String> providerCombo;
    private ComboBox<String> aiModelCombo;
    private ComboBox<String> clientCombo;
    private Button generateButton;
    private Button configureButton;
    private Label credentialStatusLabel;
    private ProgressIndicator progressIndicator;
    private ComboBox<String> goalBox;
    private ComboBox<String> prefBox;
    private ComboBox<String> activityBox;
    private TextArea notesArea;

    private List<ClientDto> clientList = new ArrayList<>();

    public void setup(ComboBox<String> providerCombo, ComboBox<String> aiModelCombo,
                      ComboBox<String> clientCombo, Button generateButton,
                      Button configureButton, Label credentialStatusLabel,
                      ProgressIndicator progressIndicator,
                      ComboBox<String> goalBox, ComboBox<String> prefBox,
                      ComboBox<String> activityBox, TextArea notesArea) {
        this.providerCombo = providerCombo;
        this.aiModelCombo = aiModelCombo;
        this.clientCombo = clientCombo;
        this.generateButton = generateButton;
        this.configureButton = configureButton;
        this.credentialStatusLabel = credentialStatusLabel;
        this.progressIndicator = progressIndicator;
        this.goalBox = goalBox;
        this.prefBox = prefBox;
        this.activityBox = activityBox;
        this.notesArea = notesArea;

        providerCombo.setItems(FXCollections.observableArrayList(PROVIDERS.keySet()));
        providerCombo.setValue("ChatGPT (OpenAI)");
        refreshModelCombo();

        loadClients();
        clientCombo.setOnShowing(e -> loadClients());
        updateCredentialStatus();

        providerCombo.setOnAction(e -> {
            refreshModelCombo();
            updateCredentialStatus();
        });
        aiModelCombo.setOnAction(e -> updateCredentialStatus());
        generateButton.setOnAction(e -> handleGenerateDiet());
        configureButton.setOnAction(e -> handleConfigureCredentials());
    }

    private void refreshModelCombo() {
        AINameEnum provider = selectedProvider();
        if (provider == null) return;
        Map<String, AIModelEnum> models = PROVIDER_MODELS.get(provider);
        if (models == null) return;
        aiModelCombo.setItems(FXCollections.observableArrayList(models.keySet()));
        aiModelCombo.getSelectionModel().selectFirst();
    }

    private AINameEnum selectedProvider() {
        String p = providerCombo.getValue();
        return p == null ? null : PROVIDERS.get(p);
    }

    private AIModelEnum selectedModel() {
        AINameEnum provider = selectedProvider();
        if (provider == null) return null;
        String m = aiModelCombo.getValue();
        if (m == null) return null;
        Map<String, AIModelEnum> models = PROVIDER_MODELS.get(provider);
        return models == null ? null : models.get(m);
    }

    private void loadClients() {
        try {
            clientList = clientService.getClients();
            List<String> names = clientList.stream()
                    .map(c -> c.getName() + " " + c.getSurname())
                    .collect(Collectors.toList());
            String current = clientCombo.getValue();
            clientCombo.setItems(FXCollections.observableArrayList(names));
            if (current != null && names.contains(current)) {
                clientCombo.setValue(current);
            } else if (!names.isEmpty()) {
                clientCombo.setValue(names.get(0));
            }
        } catch (Exception e) {
            showError("Impossibile caricare i clienti: " + e.getMessage());
        }
    }

    private void updateCredentialStatus() {
        AINameEnum provider = selectedProvider();
        AIModelEnum model = selectedModel();
        if (provider == null || model == null) return;

        boolean configured = aiUserService.hasApiKey(provider, model);
        if (configured) {
            credentialStatusLabel.setText(i18n.t("dietgen.cred.ok"));
            credentialStatusLabel.getStyleClass().removeAll("credential-missing");
            credentialStatusLabel.getStyleClass().add("credential-ok");
        } else {
            credentialStatusLabel.setText(i18n.t("dietgen.cred.missing"));
            credentialStatusLabel.getStyleClass().removeAll("credential-ok");
            credentialStatusLabel.getStyleClass().add("credential-missing");
        }
    }

    public void handleConfigureCredentials() {
        AINameEnum provider = selectedProvider();
        AIModelEnum model = selectedModel();
        if (provider == null || model == null) return;

        String displayName = providerCombo.getValue() + " - " + aiModelCombo.getValue();
        AiApiKeyDialog.show(displayName, provider).ifPresent(key -> {
            try {
                aiUserService.saveApiKey(provider, model, key);
                updateCredentialStatus();
                showInfo("API Key salvata con successo per " + displayName);
            } catch (Exception e) {
                showError("Errore nel salvataggio della API Key: " + e.getMessage());
            }
        });
    }

    public void handleGenerateDiet() {
        if (clientCombo.getValue() == null) {
            showWarning("Seleziona un cliente");
            return;
        }
        AINameEnum provider = selectedProvider();
        AIModelEnum model = selectedModel();
        if (provider == null || model == null) {
            showWarning("Seleziona un modello AI");
            return;
        }
        if (!aiUserService.hasApiKey(provider, model)) {
            showWarning("Le credenziali per " + providerCombo.getValue() + " non sono configurate.\nClicca 'Configura Credenziali' per inserirle.");
            return;
        }

        String selectedName = clientCombo.getValue();
        ClientDto selectedClient = clientList.stream()
                .filter(c -> (c.getName() + " " + c.getSurname()).equals(selectedName))
                .findFirst()
                .orElse(null);
        if (selectedClient == null) {
            showWarning("Cliente non trovato. Ricarica la lista.");
            return;
        }

        generateButton.setDisable(true);
        if (progressIndicator != null) progressIndicator.setVisible(false);

        DietGenerationProgressDialog progressDialog = new DietGenerationProgressDialog();
        progressDialog.show();

        final AINameEnum finalProvider = provider;
        final AIModelEnum finalModel = model;
        final ClientDto finalClient = selectedClient;

        Thread thread = new Thread(() -> {
            try {
                AnthropometryDto latestAnthro = null;
                try {
                    List<AnthropometryDto> visits = anthropometryService.getVisitsByClient(finalClient.getId());
                    if (!visits.isEmpty()) latestAnthro = visits.get(0);
                } catch (Exception ignored) {}

                AiDto ai = new AiDto();
                ai.setName(finalProvider);
                ai.setModel(finalModel);

                ClientRequestDto clientRequest = new ClientRequestDto();
                clientRequest.setClient(finalClient);
                clientRequest.setAnthropometry(latestAnthro);
                clientRequest.setDietDetail(buildDietDetail());

                DietRequestDto request = new DietRequestDto();
                request.setAi(ai);
                request.setClientRequest(clientRequest);

                String result = dietGeneratorService.generateDiet(request);

                progressDialog.done();
                Platform.runLater(() -> {
                    generateButton.setDisable(false);
                    showDietResult(result);
                });
            } catch (Exception e) {
                progressDialog.done();
                Platform.runLater(() -> {
                    generateButton.setDisable(false);
                    showError("Errore durante la generazione: " + e.getMessage());
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private DietDetailDto buildDietDetail() {
        DietDetailDto detail = new DietDetailDto();
        if (goalBox != null && goalBox.getValue() != null)
            detail.setPrimaryGoal(mapGoal(goalBox.getValue()));
        if (prefBox != null && prefBox.getValue() != null)
            detail.setDietaryPreference(mapPref(prefBox.getValue()));
        if (activityBox != null && activityBox.getValue() != null)
            detail.setActivityLevel(mapActivity(activityBox.getValue()));
        if (notesArea != null && notesArea.getText() != null && !notesArea.getText().isBlank())
            detail.setFoodPreferences(List.of(notesArea.getText().trim()));
        return detail;
    }

    private PrimaryGoalEnum mapGoal(String v) {
        if (i18n.t("dietgen.goal.weight.loss").equals(v)) return PrimaryGoalEnum.WEIGHT_LOSS;
        if (i18n.t("dietgen.goal.muscle").equals(v))      return PrimaryGoalEnum.MUSCLE_GAIN;
        if (i18n.t("dietgen.goal.performance").equals(v)) return PrimaryGoalEnum.MUSCLE_GAIN;
        if (i18n.t("dietgen.goal.health").equals(v))      return PrimaryGoalEnum.ENERGY_IMPROVMENT;
        return PrimaryGoalEnum.GENERAL_HEALTH;
    }

    private DietaryPreferenceEnum mapPref(String v) {
        if (i18n.t("dietgen.pref.vegetarian").equals(v)) return DietaryPreferenceEnum.VEGETARIAN;
        if (i18n.t("dietgen.pref.vegan").equals(v))      return DietaryPreferenceEnum.VEGAN;
        if (i18n.t("dietgen.pref.keto").equals(v))       return DietaryPreferenceEnum.KETO;
        return DietaryPreferenceEnum.OMNIVORE;
    }

    private ActivityLevelEnum mapActivity(String v) {
        if (i18n.t("dietgen.activity.active").equals(v))    return ActivityLevelEnum.ACTIVE;
        if (i18n.t("dietgen.activity.athlete").equals(v))   return ActivityLevelEnum.VERY_ACTIVE;
        if (i18n.t("dietgen.activity.moderate").equals(v))  return ActivityLevelEnum.MODERATE;
        return ActivityLevelEnum.SEDENTARY;
    }

    private void showDietResult(String diet) {
        String clientName    = clientCombo.getValue() != null ? clientCombo.getValue() : "Cliente";
        String providerModel = (providerCombo.getValue() != null ? providerCombo.getValue() : "")
                + (aiModelCombo.getValue() != null ? " — " + aiModelCombo.getValue() : "");
        DietResultDialog.show(diet, clientName, providerModel);
        stageManager.refreshScene("dashboard");
        stageManager.refreshScene("diet");
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(i18n.t("common.error.title"));
        a.showAndWait();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setTitle(i18n.t("common.warn.title"));
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(i18n.t("common.success.title"));
        a.showAndWait();
    }

    public void selectForDemo() {
        if (providerCombo != null) providerCombo.setValue("Claude (Anthropic)");
        if (clientCombo != null && !clientCombo.getItems().isEmpty()) {
            clientCombo.setValue(clientCombo.getItems().get(0));
        }
    }
}
