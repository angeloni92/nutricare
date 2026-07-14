package com.angeloni.nutricare.ui;

import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.ui.controller.ClientController;
import com.angeloni.nutricare.ui.controller.DashboardController;
import com.angeloni.nutricare.ui.controller.DietController;
import com.angeloni.nutricare.ui.controller.DietGeneratorController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class SceneBuilder {

    private final StageManager stageManager;
    private final DashboardController dashboardController;
    private final ClientController clientController;
    private final DietController dietController;
    private final DietGeneratorController dietGeneratorController;
    private final ClientRepository clientRepository;
    private final DietResultRepository dietResultRepository;

    public SceneBuilder(StageManager stageManager,
                        DashboardController dashboardController,
                        ClientController clientController,
                        DietController dietController,
                        DietGeneratorController dietGeneratorController,
                        ClientRepository clientRepository,
                        DietResultRepository dietResultRepository) {
        this.stageManager = stageManager;
        this.dashboardController = dashboardController;
        this.clientController = clientController;
        this.dietController = dietController;
        this.dietGeneratorController = dietGeneratorController;
        this.clientRepository = clientRepository;
        this.dietResultRepository = dietResultRepository;
    }

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

        VBox logoArea = new VBox(3);
        logoArea.getStyleClass().add("sidebar-logo-area");
        Label appName = new Label("Nutricare");
        appName.getStyleClass().add("sidebar-logo");
        Label appSub = new Label("Nutrition Management");
        appSub.getStyleClass().add("sidebar-subtitle");
        logoArea.getChildren().addAll(appName, appSub);

        VBox navContainer = new VBox(2);
        navContainer.setPadding(new Insets(12, 10, 12, 10));
        VBox.setVgrow(navContainer, Priority.ALWAYS);

        Label menuLabel = new Label("NAVIGAZIONE");
        menuLabel.getStyleClass().add("nav-section-label");

        Button dashBtn = buildNavItem("Dashboard", "dashboard".equals(activeScene));
        dashBtn.setOnAction(e -> stageManager.switchScene("dashboard"));

        Button clientBtn = buildNavItem("Clienti", "client".equals(activeScene));
        clientBtn.setOnAction(e -> stageManager.switchScene("client"));

        Button dietListBtn = buildNavItem("Storico Diete", "diet".equals(activeScene));
        dietListBtn.setOnAction(e -> stageManager.switchScene("diet"));

        Button dietGenBtn = buildNavItem("Genera Dieta AI", "diet-generator".equals(activeScene));
        dietGenBtn.setOnAction(e -> stageManager.switchScene("diet-generator"));

        navContainer.getChildren().addAll(menuLabel, dashBtn, clientBtn, dietListBtn, dietGenBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox exitArea = new VBox(4);
        exitArea.setPadding(new Insets(8, 10, 20, 10));
        Button exitBtn = new Button("Esci dall'applicazione");
        exitBtn.getStyleClass().add("nav-item-exit");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setOnAction(e -> System.exit(0));
        exitArea.getChildren().add(exitBtn);

        sidebar.getChildren().addAll(logoArea, navContainer, spacer, exitArea);
        return sidebar;
    }

    private Button buildNavItem(String text, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-item");
        if (active) {
            btn.getStyleClass().add("active");
        }
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    // ───────────────────────────── DASHBOARD ─────────────────────────────

    public Scene buildDashboardScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("dashboard"));

        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Panoramica delle attivita");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        long clientCount = safeCount(() -> clientRepository.count());
        long dietCount = safeCount(() -> dietResultRepository.count());

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            buildStatCard(String.valueOf(clientCount), "Clienti", "Pazienti registrati"),
            buildStatCard(String.valueOf(dietCount), "Diete Generate", "Piani nutrizionali AI"),
            buildStatCard("6", "Modelli AI", "Disponibili"),
            buildStatCard("v1.0", "Versione", "Nutricare Desktop")
        );

        VBox welcomeCard = new VBox(8);
        welcomeCard.getStyleClass().add("card");
        Label welcomeTitle = new Label("Benvenuto in Nutricare");
        welcomeTitle.getStyleClass().add("card-title");
        welcomeTitle.setStyle("-fx-font-size: 17px;");
        Label welcomeText = new Label(
            "Gestisci i tuoi pazienti, genera piani nutrizionali personalizzati con l'intelligenza artificiale\n" +
            "e tieni traccia dei progressi nel tempo."
        );
        welcomeText.getStyleClass().add("card-subtitle");
        welcomeText.setWrapText(true);
        welcomeCard.getChildren().addAll(welcomeTitle, welcomeText);

        VBox actionsCard = new VBox(12);
        actionsCard.getStyleClass().add("card");
        Label actionsTitle = new Label("Azioni Rapide");
        actionsTitle.getStyleClass().add("card-title");
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button goClients = new Button("Gestisci Clienti");
        goClients.getStyleClass().add("btn-primary");
        goClients.setOnAction(e -> stageManager.switchScene("client"));

        Button goDietGen = new Button("Genera Dieta AI");
        goDietGen.getStyleClass().add("btn-success");
        goDietGen.setOnAction(e -> stageManager.switchScene("diet-generator"));

        Button goDietList = new Button("Storico Diete");
        goDietList.getStyleClass().add("btn-secondary");
        goDietList.setOnAction(e -> stageManager.switchScene("diet"));

        actions.getChildren().addAll(goClients, goDietGen, goDietList);
        actionsCard.getChildren().addAll(actionsTitle, actions);

        content.getChildren().addAll(statsRow, welcomeCard, actionsCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    private VBox buildStatCard(String value, String label, String description) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-card-value");

        Label labelEl = new Label(label);
        labelEl.getStyleClass().add("stat-card-label");

        Label descEl = new Label(description);
        descEl.getStyleClass().add("stat-card-accent");

        card.getChildren().addAll(valueLabel, labelEl, descEl);
        return card;
    }

    private long safeCount(java.util.concurrent.Callable<Long> fn) {
        try {
            return fn.call();
        } catch (Exception e) {
            return 0L;
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
        searchField.setPromptText("Cerca clienti...");

        Button addBtn = new Button("+ Nuovo Cliente");
        addBtn.getStyleClass().add("btn-primary");

        header.getChildren().addAll(headerBlock, headerSpacer, searchField, addBtn);
        root.setTop(header);

        TableView<String> clientTable = new TableView<>();
        clientTable.getStyleClass().add("styled-table");
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clientTable.setPlaceholder(new Label("Nessun cliente trovato. Crea il primo cliccando \"+ Nuovo Cliente\"."));

        TableColumn<String, String> nameCol = new TableColumn<>("Nome");
        TableColumn<String, String> surnameCol = new TableColumn<>("Cognome");
        TableColumn<String, String> ageCol = new TableColumn<>("Eta");
        ageCol.setMaxWidth(80);
        TableColumn<String, String> countryCol = new TableColumn<>("Paese");

        clientTable.getColumns().addAll(nameCol, surnameCol, ageCol, countryCol);

        VBox tableWrapper = new VBox();
        tableWrapper.getStyleClass().add("content-area");
        VBox.setVgrow(clientTable, Priority.ALWAYS);
        tableWrapper.getChildren().add(clientTable);
        root.setCenter(tableWrapper);

        HBox footer = new HBox(10);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("Modifica");
        editBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button("Elimina");
        deleteBtn.getStyleClass().add("btn-danger");

        footer.getChildren().addAll(editBtn, deleteBtn);
        root.setBottom(footer);

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
        Label subtitle = new Label("Piani nutrizionali generati con intelligenza artificiale");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("Tutte le diete", "Ultimi 7 giorni", "Ultimo mese");
        filterBox.setValue("Tutte le diete");
        filterBox.getStyleClass().add("form-combo");
        filterBox.setPrefWidth(180);

        Button genBtn = new Button("Genera Nuova Dieta");
        genBtn.getStyleClass().add("btn-success");
        genBtn.setOnAction(e -> stageManager.switchScene("diet-generator"));

        header.getChildren().addAll(headerBlock, headerSpacer, filterBox, genBtn);
        root.setTop(header);

        TableView<String> dietTable = new TableView<>();
        dietTable.getStyleClass().add("styled-table");
        dietTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dietTable.setPlaceholder(new Label("Nessuna dieta trovata. Genera la prima cliccando \"Genera Nuova Dieta\"."));

        TableColumn<String, String> clientCol = new TableColumn<>("Cliente");
        TableColumn<String, String> modelCol = new TableColumn<>("Modello AI");
        TableColumn<String, String> dateCol = new TableColumn<>("Data Generazione");

        dietTable.getColumns().addAll(clientCol, modelCol, dateCol);

        VBox tableWrapper = new VBox();
        tableWrapper.getStyleClass().add("content-area");
        VBox.setVgrow(dietTable, Priority.ALWAYS);
        tableWrapper.getChildren().add(dietTable);
        root.setCenter(tableWrapper);

        HBox footer = new HBox(10);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("Visualizza");
        viewBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button("Elimina");
        deleteBtn.getStyleClass().add("btn-danger");

        footer.getChildren().addAll(viewBtn, deleteBtn);
        root.setBottom(footer);

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
        Label title = new Label("Generatore Dieta AI");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Configura i parametri e genera un piano nutrizionale personalizzato");
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        // Row 1: Patient + AI Model cards
        HBox row1 = new HBox(16);
        row1.setAlignment(Pos.TOP_LEFT);

        VBox patientCard = new VBox(10);
        patientCard.getStyleClass().add("card");
        HBox.setHgrow(patientCard, Priority.ALWAYS);
        Label patientTitle = new Label("Selezione Paziente");
        patientTitle.getStyleClass().add("card-title");
        Label clientLabel = new Label("Cliente");
        clientLabel.getStyleClass().add("form-label");
        ComboBox<String> clientCombo = new ComboBox<>();
        clientCombo.getStyleClass().add("form-combo");
        clientCombo.setMaxWidth(Double.MAX_VALUE);
        clientCombo.setPromptText("Seleziona un paziente...");
        patientCard.getChildren().addAll(patientTitle, clientLabel, clientCombo);

        VBox aiCard = new VBox(10);
        aiCard.getStyleClass().add("card");
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        Label aiTitle = new Label("Modello AI");
        aiTitle.getStyleClass().add("card-title");
        Label modelLabel = new Label("Seleziona modello");
        modelLabel.getStyleClass().add("form-label");
        ComboBox<String> aiModelCombo = new ComboBox<>();
        aiModelCombo.getStyleClass().add("form-combo");
        aiModelCombo.setMaxWidth(Double.MAX_VALUE);
        aiModelCombo.setPromptText("Scegli un modello AI...");

        HBox credRow = new HBox(10);
        credRow.setAlignment(Pos.CENTER_LEFT);
        Label credentialStatusLabel = new Label();
        credentialStatusLabel.getStyleClass().add("credential-missing");
        Button configureButton = new Button("Configura Credenziali");
        configureButton.getStyleClass().add("btn-configure");
        credRow.getChildren().addAll(credentialStatusLabel, configureButton);

        aiCard.getChildren().addAll(aiTitle, modelLabel, aiModelCombo, credRow);
        row1.getChildren().addAll(patientCard, aiCard);

        // Row 2: Parameters card
        VBox paramsCard = new VBox(14);
        paramsCard.getStyleClass().add("card");
        Label paramsTitle = new Label("Parametri Dieta");
        paramsTitle.getStyleClass().add("card-title");

        HBox paramsRow = new HBox(20);
        paramsRow.setAlignment(Pos.TOP_LEFT);

        VBox goalGroup = new VBox(6);
        HBox.setHgrow(goalGroup, Priority.ALWAYS);
        Label goalLabel = new Label("Obiettivo Principale");
        goalLabel.getStyleClass().add("form-label");
        ComboBox<String> goalBox = new ComboBox<>();
        goalBox.getItems().addAll("Perdita Peso", "Aumento Massa Muscolare", "Mantenimento", "Miglioramento Salute");
        goalBox.setValue("Mantenimento");
        goalBox.getStyleClass().add("form-combo");
        goalBox.setMaxWidth(Double.MAX_VALUE);
        goalGroup.getChildren().addAll(goalLabel, goalBox);

        VBox prefGroup = new VBox(6);
        HBox.setHgrow(prefGroup, Priority.ALWAYS);
        Label prefLabel = new Label("Preferenza Alimentare");
        prefLabel.getStyleClass().add("form-label");
        ComboBox<String> prefBox = new ComboBox<>();
        prefBox.getItems().addAll("Onnivoro", "Vegetariano", "Vegano", "Senza Glutine", "Senza Lattosio");
        prefBox.setValue("Onnivoro");
        prefBox.getStyleClass().add("form-combo");
        prefBox.setMaxWidth(Double.MAX_VALUE);
        prefGroup.getChildren().addAll(prefLabel, prefBox);

        paramsRow.getChildren().addAll(goalGroup, prefGroup);
        paramsCard.getChildren().addAll(paramsTitle, paramsRow);

        // Notes card
        VBox notesCard = new VBox(10);
        notesCard.getStyleClass().add("card");
        Label notesTitle = new Label("Note Aggiuntive");
        notesTitle.getStyleClass().add("card-title");
        Label notesSubtitle = new Label("Allergie, intolleranze o preferenze particolari da comunicare all'AI");
        notesSubtitle.getStyleClass().add("card-subtitle");
        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("form-field");
        notesArea.setPrefHeight(100);
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

        Button generateBtn = new Button("Genera Piano Nutrizionale");
        generateBtn.getStyleClass().add("btn-success");

        footer.getChildren().addAll(progressIndicator, footerSpacer, generateBtn);
        root.setBottom(footer);

        dietGeneratorController.setup(aiModelCombo, clientCombo, generateBtn,
                configureButton, credentialStatusLabel, progressIndicator);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }
}
