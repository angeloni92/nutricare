package com.angeloni.nutricare.ui;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.service.BackupService;
import com.angeloni.nutricare.ui.controller.ClientController;
import com.angeloni.nutricare.ui.controller.DashboardController;
import com.angeloni.nutricare.ui.controller.DietController;
import com.angeloni.nutricare.ui.controller.DietGeneratorController;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class
SceneBuilder {

    private final StageManager stageManager;
    private final DashboardController dashboardController;
    private final ClientController clientController;
    private final DietController dietController;
    private final DietGeneratorController dietGeneratorController;
    private final ClientRepository clientRepository;
    private final DietResultRepository dietResultRepository;
    private final BackupService backupService;

    public SceneBuilder(StageManager stageManager,
                        DashboardController dashboardController,
                        ClientController clientController,
                        DietController dietController,
                        DietGeneratorController dietGeneratorController,
                        ClientRepository clientRepository,
                        DietResultRepository dietResultRepository,
                        BackupService backupService) {
        this.stageManager = stageManager;
        this.dashboardController = dashboardController;
        this.clientController = clientController;
        this.dietController = dietController;
        this.dietGeneratorController = dietGeneratorController;
        this.clientRepository = clientRepository;
        this.dietResultRepository = dietResultRepository;
        this.backupService = backupService;
    }

    private Label dashClientCountLabel;
    private Label dashDietCountLabel;

    private void addStyles(Scene scene) {
        String css = getClass().getResource("/styles/nutricare.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    // ───────────────────────────── SIDEBAR ─────────────────────────────

    private VBox buildSidebar(String activeScene) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);

        // Logo area
        VBox logoArea = new VBox(4);
        logoArea.getStyleClass().add("sidebar-logo-area");

        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER_LEFT);
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/images/logo-64.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(38);
            logoView.setFitHeight(38);
            logoRow.getChildren().add(logoView);
        } catch (Exception ignored) {}

        VBox logoText = new VBox(1);
        Label appName = new Label("NutriCare");
        appName.getStyleClass().add("sidebar-logo");
        Label appSub = new Label("Nutrition Management");
        appSub.getStyleClass().add("sidebar-subtitle");
        logoText.getChildren().addAll(appName, appSub);
        logoRow.getChildren().add(logoText);

        logoArea.getChildren().add(logoRow);

        // Nav section
        VBox navContainer = new VBox(2);
        navContainer.setPadding(new Insets(16, 10, 8, 10));

        Label menuLabel = new Label("MENU");
        menuLabel.getStyleClass().add("nav-section-label");

        Button dashBtn   = buildNavItem("  Dashboard",        "dashboard".equals(activeScene));
        Button clientBtn = buildNavItem("  Clienti",          "client".equals(activeScene));
        Button dietBtn   = buildNavItem("  Storico Diete",    "diet".equals(activeScene));
        Button genBtn    = buildNavItem("  Genera Dieta AI",  "diet-generator".equals(activeScene));

        dashBtn.setOnAction(e   -> stageManager.switchScene("dashboard"));
        clientBtn.setOnAction(e -> stageManager.switchScene("client"));
        dietBtn.setOnAction(e   -> stageManager.switchScene("diet"));
        genBtn.setOnAction(e    -> stageManager.switchScene("diet-generator"));

        navContainer.getChildren().addAll(menuLabel, dashBtn, clientBtn, dietBtn, genBtn);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom area
        VBox bottomArea = new VBox(6);
        bottomArea.setPadding(new Insets(8, 10, 16, 10));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e293b;");
        sep.setPadding(new Insets(0, 0, 4, 0));

        Button exitBtn = new Button("  Esci dall'applicazione");
        exitBtn.getStyleClass().add("nav-item-exit");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setOnAction(e -> System.exit(0));

        Label versionLabel = new Label("v1.0.0");
        versionLabel.getStyleClass().add("sidebar-subtitle");
        versionLabel.setPadding(new Insets(4, 8, 0, 8));

        bottomArea.getChildren().addAll(sep, exitBtn, versionLabel);

        sidebar.getChildren().addAll(logoArea, navContainer, spacer, bottomArea);
        return sidebar;
    }

    private Button buildNavItem(String text, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-item");
        if (active) btn.getStyleClass().add("active");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    // ───────────────────────────── DASHBOARD ─────────────────────────────

    public Scene buildDashboardScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("dashboard"));

        // Header con greeting dinamico
        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label(getGreeting());
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Ecco una panoramica della tua attivita");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(24);
        content.getStyleClass().add("content-area");

        long clientCount = safeCount(() -> clientRepository.count());
        long dietCount   = safeCount(() -> dietResultRepository.count());

        // Stat cards
        dashClientCountLabel = new Label(String.valueOf(clientCount));
        dashDietCountLabel   = new Label(String.valueOf(dietCount));
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            buildDynamicStatCard(dashClientCountLabel, "Clienti",        "Pazienti registrati",       "#6366f1", "#eef2ff"),
            buildDynamicStatCard(dashDietCountLabel,   "Diete Generate", "Piani nutrizionali AI",     "#10b981", "#ecfdf5"),
            buildStatCard("28",                        "Modelli AI",     "ChatGPT, Claude, Gemini",   "#f59e0b", "#fffbeb"),
            buildStatCard("3",                         "Provider",       "OpenAI, Anthropic, Google", "#8b5cf6", "#f5f3ff")
        );

        // Quick actions
        VBox actionsCard = new VBox(14);
        actionsCard.getStyleClass().add("card");
        Label actionsTitle = new Label("Azioni Rapide");
        actionsTitle.getStyleClass().add("card-title");

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button goClients = new Button("  Gestisci Clienti");
        goClients.getStyleClass().add("btn-primary");
        goClients.setOnAction(e -> stageManager.switchScene("client"));
        Button goDietGen = new Button("  Genera Dieta AI");
        goDietGen.getStyleClass().add("btn-success");
        goDietGen.setOnAction(e -> stageManager.switchScene("diet-generator"));
        Button goDietList = new Button("  Storico Diete");
        goDietList.getStyleClass().add("btn-secondary");
        goDietList.setOnAction(e -> stageManager.switchScene("diet"));
        Button backupBtn = new Button("  Backup Dati");
        backupBtn.getStyleClass().add("btn-secondary");
        backupBtn.setOnAction(e -> handleBackup());
        actions.getChildren().addAll(goClients, goDietGen, goDietList, backupBtn);
        actionsCard.getChildren().addAll(actionsTitle, actions);

        // Info card
        HBox infoRow = new HBox(16);
        VBox infoCard1 = buildInfoCard(
            "Come iniziare",
            "1. Aggiungi un paziente dalla sezione Clienti\n" +
            "2. Vai su Genera Dieta AI\n" +
            "3. Seleziona il provider AI e configura le credenziali\n" +
            "4. Seleziona il paziente e genera il piano"
        );
        VBox infoCard2 = buildInfoCard(
            "Modelli AI supportati (28 totali)",
            "ChatGPT: GPT-4o, GPT-4o mini, o3, o3-mini, o4-mini, o1, o1-mini, GPT-4 Turbo, GPT-4, GPT-3.5\n" +
            "Claude: Fable 5, Sonnet 5, Sonnet 4, Opus 4.8, Opus 4, 3.7 Sonnet, 3.5 Sonnet, 4.5 Haiku, 3.5 Haiku, 3 Opus, 3 Sonnet, 3 Haiku\n" +
            "Gemini: 2.5 Pro, 2.5 Flash, 2.0 Flash, 2.0 Flash Lite, 1.5 Flash, 1.5 Pro\n\n" +
            "Configura le API Key nella schermata Genera Dieta"
        );
        HBox.setHgrow(infoCard1, Priority.ALWAYS);
        HBox.setHgrow(infoCard2, Priority.ALWAYS);
        infoRow.getChildren().addAll(infoCard1, infoCard2);

        content.getChildren().addAll(statsRow, actionsCard, infoRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        stageManager.registerRefresh("dashboard", this::refreshDashboard);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    public void refreshDashboard() {
        if (dashClientCountLabel != null)
            dashClientCountLabel.setText(String.valueOf(safeCount(() -> clientRepository.count())));
        if (dashDietCountLabel != null)
            dashDietCountLabel.setText(String.valueOf(safeCount(() -> dietResultRepository.count())));
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "  Buongiorno!";
        if (hour < 18) return "  Buon pomeriggio!";
        return "  Buonasera!";
    }

    private VBox buildStatCard(String value, String label, String description,
                                String accentColor, String bgColor) {
        VBox card = new VBox(6);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";"
        );

        Label labelEl = new Label(label);
        labelEl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label descEl = new Label(description);
        descEl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(valueLabel, labelEl, descEl);
        return card;
    }

    private VBox buildDynamicStatCard(Label valueLabel, String label, String description,
                                      String accentColor, String bgColor) {
        valueLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");
        VBox card = new VBox(6);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        Label labelEl = new Label(label);
        labelEl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label descEl = new Label(description);
        descEl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        card.getChildren().addAll(valueLabel, labelEl, descEl);
        return card;
    }

    private VBox buildInfoCard(String title, String text) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("card-subtitle");
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151; -fx-line-spacing: 3;");
        card.getChildren().addAll(titleLabel, textLabel);
        return card;
    }

    private long safeCount(java.util.concurrent.Callable<Long> fn) {
        try {
            return fn.call();
        } catch (Exception e) {
            return 0L;
        }
    }

    private void handleBackup() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Scegli cartella di destinazione backup");
        chooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        java.io.File dir = chooser.showDialog(stageManager.getPrimaryStage());
        if (dir == null) return;
        try {
            Path backupFile = backupService.backup(dir.toPath());
            new Alert(Alert.AlertType.INFORMATION,
                    "Backup completato con successo!\n\n" + backupFile.toAbsolutePath(),
                    ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Errore durante il backup:\n" + ex.getMessage(),
                    ButtonType.OK).showAndWait();
        }
    }

    // ───────────────────────────── CLIENTS ─────────────────────────────

    public Scene buildClientScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("client"));

        HBox header = new HBox(16);
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerBlock = new VBox(2);
        Label title = new Label("Gestione Clienti");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Visualizza e gestisci i tuoi pazienti");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("  Cerca clienti...");

        Button addBtn = new Button("  Nuovo Cliente");
        addBtn.getStyleClass().add("btn-primary");

        header.getChildren().addAll(headerBlock, headerSpacer, searchField, addBtn);
        root.setTop(header);

        // ─── Client table ────────────────────────────────────────────────
        TableView<ClientDto> clientTable = new TableView<>();
        clientTable.getStyleClass().add("styled-table");
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clientTable.setPlaceholder(new Label("Nessun cliente. Crea il primo cliccando \"Nuovo Cliente\"."));

        TableColumn<ClientDto, String> nameCol    = new TableColumn<>("Nome");
        TableColumn<ClientDto, String> surnameCol = new TableColumn<>("Cognome");
        TableColumn<ClientDto, String> ageCol     = new TableColumn<>("Eta");
        TableColumn<ClientDto, String> countryCol = new TableColumn<>("Paese");

        nameCol.setCellValueFactory(cd    -> new SimpleStringProperty(cd.getValue().getName()    != null ? cd.getValue().getName()    : ""));
        surnameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSurname() != null ? cd.getValue().getSurname() : ""));
        ageCol.setCellValueFactory(cd     -> new SimpleStringProperty(cd.getValue().getAge()     != null ? cd.getValue().getAge().toString() : ""));
        countryCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCountry() != null ? cd.getValue().getCountry() : ""));

        ageCol.setMaxWidth(80);
        clientTable.getColumns().addAll(nameCol, surnameCol, ageCol, countryCol);

        VBox clientPanel = new VBox(clientTable);
        clientPanel.getStyleClass().add("content-area");
        VBox.setVgrow(clientTable, Priority.ALWAYS);

        // ─── History table ───────────────────────────────────────────────
        Label historyLabel = new Label("Seleziona un cliente per vedere lo storico visite");
        historyLabel.getStyleClass().add("card-title");

        TableView<AnthropometryDto> historyTable = new TableView<>();
        historyTable.getStyleClass().add("styled-table");
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPlaceholder(new Label("Nessuna visita registrata."));

        TableColumn<AnthropometryDto, String> dateCol = new TableColumn<>("Data visita");
        TableColumn<AnthropometryDto, String> hCol    = new TableColumn<>("Altezza (cm)");
        TableColumn<AnthropometryDto, String> wCol    = new TableColumn<>("Peso (kg)");
        TableColumn<AnthropometryDto, String> bmiCol  = new TableColumn<>("BMI");
        TableColumn<AnthropometryDto, String> plCol   = new TableColumn<>("Pliche");
        TableColumn<AnthropometryDto, String> cirCol  = new TableColumn<>("Circonferenze");

        DateTimeFormatter visitFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCreatedAt() != null ? cd.getValue().getCreatedAt().format(visitFmt) : "-"));
        hCol.setCellValueFactory(cd   -> new SimpleStringProperty(
                cd.getValue().getHeight() != null ? String.format("%.1f", cd.getValue().getHeight()) : "-"));
        wCol.setCellValueFactory(cd   -> new SimpleStringProperty(
                cd.getValue().getWeight() != null ? String.format("%.1f", cd.getValue().getWeight()) : "-"));
        bmiCol.setCellValueFactory(cd -> {
            Double h = cd.getValue().getHeight(), w = cd.getValue().getWeight();
            if (h == null || w == null || h <= 0) return new SimpleStringProperty("-");
            return new SimpleStringProperty(String.format("%.1f", w / Math.pow(h / 100.0, 2)));
        });
        plCol.setCellValueFactory(cd  -> new SimpleStringProperty(cd.getValue().getFold()          != null ? "Si" : "-"));
        cirCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCircumference() != null ? "Si" : "-"));

        historyTable.getColumns().addAll(dateCol, hCol, wCol, bmiCol, plCol, cirCol);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        VBox historyPanel = new VBox(8, historyLabel, historyTable);
        historyPanel.getStyleClass().add("content-area");
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        // ─── Anthropometric detail pane ──────────────────────────────────
        VBox visitDetailBox = new VBox(12);
        visitDetailBox.setPadding(new Insets(16));
        visitDetailBox.setStyle("-fx-background-color: white;");
        Label detailPlaceholder = new Label("Seleziona una visita per vedere il dettaglio antropometrico");
        detailPlaceholder.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        visitDetailBox.getChildren().add(detailPlaceholder);

        ScrollPane detailScroll = new ScrollPane(visitDetailBox);
        detailScroll.setFitToWidth(true);
        detailScroll.setStyle("-fx-background: white; -fx-background-color: white;");

        SplitPane bottomSplit = new SplitPane(historyPanel, detailScroll);
        bottomSplit.setDividerPositions(0.55);

        SplitPane splitPane = new SplitPane(clientPanel, bottomSplit);
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.45);
        root.setCenter(splitPane);

        // ─── Footer ──────────────────────────────────────────────────────
        HBox footer = new HBox(10);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_LEFT);

        Button exportExcelBtn = new Button("  Esporta Excel");
        exportExcelBtn.getStyleClass().add("btn-secondary");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button newVisitBtn = new Button("  Nuova Visita");
        newVisitBtn.getStyleClass().add("btn-success");
        Button editBtn   = new Button("  Modifica");
        editBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button("  Elimina");
        deleteBtn.getStyleClass().add("btn-danger");
        footer.getChildren().addAll(exportExcelBtn, footerSpacer, newVisitBtn, editBtn, deleteBtn);
        root.setBottom(footer);

        clientController.setup(clientTable, searchField, addBtn, editBtn, deleteBtn,
                newVisitBtn, historyTable, historyLabel, visitDetailBox, exportExcelBtn);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    // ───────────────────────────── DIET LIST ─────────────────────────────

    public Scene buildDietScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("diet"));

        HBox header = new HBox(16);
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerBlock = new VBox(2);
        Label title = new Label("Storico Diete");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Piani nutrizionali generati con l'intelligenza artificiale");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button genBtn = new Button("  Genera Nuova Dieta");
        genBtn.getStyleClass().add("btn-success");
        genBtn.setOnAction(e -> stageManager.switchScene("diet-generator"));

        header.getChildren().addAll(headerBlock, headerSpacer, genBtn);
        root.setTop(header);

        TableView<DietResultEntity> dietTable = new TableView<>();
        dietTable.getStyleClass().add("styled-table");
        dietTable.setPlaceholder(new Label("Nessuna dieta generata. Usa \"Genera Nuova Dieta\" per iniziare."));

        TextField searchField = new TextField();
        searchField.setPromptText("Cerca per cliente...");
        searchField.setMaxWidth(300);
        searchField.setStyle(
            "-fx-font-size: 13px; -fx-background-radius: 8; -fx-border-radius: 8;" +
            "-fx-border-color: #d1d5db; -fx-padding: 7 12 7 12;"
        );

        HBox searchBar = new HBox(searchField);
        searchBar.setStyle("-fx-padding: 12 24 8 24; -fx-background-color: transparent;");
        searchBar.setAlignment(Pos.CENTER_LEFT);

        VBox tableWrapper = new VBox();
        tableWrapper.getStyleClass().add("content-area");
        VBox.setVgrow(dietTable, Priority.ALWAYS);
        tableWrapper.getChildren().addAll(searchBar, dietTable);
        root.setCenter(tableWrapper);

        HBox footer = new HBox(10);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button viewBtn   = new Button("  Visualizza");
        viewBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button("  Elimina");
        deleteBtn.getStyleClass().add("btn-danger");
        footer.getChildren().addAll(viewBtn, deleteBtn);
        root.setBottom(footer);

        dietController.setup(dietTable, viewBtn, deleteBtn);
        dietController.setupSearchField(searchField);
        stageManager.registerRefresh("diet", dietController::refresh);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    // ───────────────────────────── DIET GENERATOR ─────────────────────────────

    public Scene buildDietGeneratorScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("diet-generator"));

        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label("Genera Dieta con AI");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Seleziona il provider AI, configura le credenziali e genera un piano nutrizionale personalizzato");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        // Row 1: paziente + AI
        HBox row1 = new HBox(16);
        row1.setAlignment(Pos.TOP_LEFT);

        // Card Paziente
        VBox patientCard = new VBox(10);
        patientCard.getStyleClass().add("card");
        HBox.setHgrow(patientCard, Priority.ALWAYS);
        Label patientTitle = new Label("  Selezione Paziente");
        patientTitle.getStyleClass().add("card-title");
        Label clientLabel = new Label("Cliente");
        clientLabel.getStyleClass().add("form-label");
        ComboBox<String> clientCombo = new ComboBox<>();
        clientCombo.getStyleClass().add("form-combo");
        clientCombo.setMaxWidth(Double.MAX_VALUE);
        clientCombo.setPromptText("Seleziona un paziente...");
        patientCard.getChildren().addAll(patientTitle, clientLabel, clientCombo);

        // Card AI — provider → model a cascata
        VBox aiCard = new VBox(10);
        aiCard.getStyleClass().add("card");
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        Label aiTitle = new Label("  Configurazione AI");
        aiTitle.getStyleClass().add("card-title");

        Label providerLabel = new Label("Provider AI");
        providerLabel.getStyleClass().add("form-label");
        ComboBox<String> providerCombo = new ComboBox<>();
        providerCombo.getStyleClass().add("form-combo");
        providerCombo.setMaxWidth(Double.MAX_VALUE);

        Label modelLabel = new Label("Modello");
        modelLabel.getStyleClass().add("form-label");
        ComboBox<String> aiModelCombo = new ComboBox<>();
        aiModelCombo.getStyleClass().add("form-combo");
        aiModelCombo.setMaxWidth(Double.MAX_VALUE);
        aiModelCombo.setPromptText("Scegli prima il provider...");

        HBox credRow = new HBox(10);
        credRow.setAlignment(Pos.CENTER_LEFT);
        Label credentialStatusLabel = new Label("Credenziali non configurate");
        credentialStatusLabel.getStyleClass().add("credential-missing");
        Region credSpacer = new Region();
        HBox.setHgrow(credSpacer, Priority.ALWAYS);
        Button configureButton = new Button("  Configura Credenziali");
        configureButton.getStyleClass().add("btn-configure");
        credRow.getChildren().addAll(credentialStatusLabel, credSpacer, configureButton);

        aiCard.getChildren().addAll(aiTitle, providerLabel, providerCombo, modelLabel, aiModelCombo, credRow);
        row1.getChildren().addAll(patientCard, aiCard);

        // Card Parametri
        VBox paramsCard = new VBox(14);
        paramsCard.getStyleClass().add("card");
        Label paramsTitle = new Label("  Parametri Dieta");
        paramsTitle.getStyleClass().add("card-title");

        HBox paramsRow = new HBox(20);
        paramsRow.setAlignment(Pos.TOP_LEFT);

        VBox goalGroup = new VBox(6);
        HBox.setHgrow(goalGroup, Priority.ALWAYS);
        Label goalLabel = new Label("Obiettivo Principale");
        goalLabel.getStyleClass().add("form-label");
        ComboBox<String> goalBox = new ComboBox<>();
        goalBox.getItems().addAll("Perdita Peso", "Aumento Massa Muscolare", "Mantenimento",
                                   "Miglioramento Salute", "Performance Atletica");
        goalBox.setValue("Mantenimento");
        goalBox.getStyleClass().add("form-combo");
        goalBox.setMaxWidth(Double.MAX_VALUE);
        goalGroup.getChildren().addAll(goalLabel, goalBox);

        VBox prefGroup = new VBox(6);
        HBox.setHgrow(prefGroup, Priority.ALWAYS);
        Label prefLabel = new Label("Preferenza Alimentare");
        prefLabel.getStyleClass().add("form-label");
        ComboBox<String> prefBox = new ComboBox<>();
        prefBox.getItems().addAll("Onnivoro", "Vegetariano", "Vegano",
                                   "Senza Glutine", "Senza Lattosio", "Ketogenica");
        prefBox.setValue("Onnivoro");
        prefBox.getStyleClass().add("form-combo");
        prefBox.setMaxWidth(Double.MAX_VALUE);
        prefGroup.getChildren().addAll(prefLabel, prefBox);

        VBox activityGroup = new VBox(6);
        HBox.setHgrow(activityGroup, Priority.ALWAYS);
        Label activityLabel = new Label("Livello di Attivita");
        activityLabel.getStyleClass().add("form-label");
        ComboBox<String> activityBox = new ComboBox<>();
        activityBox.getItems().addAll("Sedentario", "Leggermente Attivo",
                                       "Moderatamente Attivo", "Molto Attivo", "Atleta");
        activityBox.setValue("Moderatamente Attivo");
        activityBox.getStyleClass().add("form-combo");
        activityBox.setMaxWidth(Double.MAX_VALUE);
        activityGroup.getChildren().addAll(activityLabel, activityBox);

        paramsRow.getChildren().addAll(goalGroup, prefGroup, activityGroup);
        paramsCard.getChildren().addAll(paramsTitle, paramsRow);

        // Card Note
        VBox notesCard = new VBox(10);
        notesCard.getStyleClass().add("card");
        Label notesTitle = new Label("  Note Aggiuntive");
        notesTitle.getStyleClass().add("card-title");
        Label notesSubtitle = new Label("Allergie, intolleranze o preferenze particolari da comunicare all'AI");
        notesSubtitle.getStyleClass().add("card-subtitle");
        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("form-field");
        notesArea.setPrefHeight(90);
        notesArea.setWrapText(true);
        notesArea.setPromptText("Es: evitare i latticini la sera, preferire proteine vegetali a pranzo...");
        notesCard.getChildren().addAll(notesTitle, notesSubtitle, notesArea);

        content.getChildren().addAll(row1, paramsCard, notesCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // Footer
        HBox footer = new HBox(12);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(26, 26);
        progressIndicator.setVisible(false);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button generateBtn = new Button("  Genera Piano Nutrizionale");
        generateBtn.getStyleClass().add("btn-success");

        footer.getChildren().addAll(progressIndicator, footerSpacer, generateBtn);
        root.setBottom(footer);

        dietGeneratorController.setup(providerCombo, aiModelCombo, clientCombo,
                generateBtn, configureButton, credentialStatusLabel, progressIndicator,
                goalBox, prefBox, activityBox, notesArea);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }
}
