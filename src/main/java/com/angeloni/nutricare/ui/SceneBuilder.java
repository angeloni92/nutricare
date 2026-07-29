package com.angeloni.nutricare.ui;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.service.BackupService;
import com.angeloni.nutricare.service.I18nService;
import com.angeloni.nutricare.service.LicenseService;
import com.angeloni.nutricare.service.UserContextService;
import com.angeloni.nutricare.entity.AuditLogEntity;
import com.angeloni.nutricare.ui.controller.AuditLogController;
import com.angeloni.nutricare.ui.controller.ClientController;
import com.angeloni.nutricare.ui.controller.DashboardController;
import com.angeloni.nutricare.ui.controller.DietController;
import com.angeloni.nutricare.ui.controller.DietGeneratorController;
import com.angeloni.nutricare.ui.controller.TrendController;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

@Component
public class SceneBuilder {

    private final StageManager stageManager;
    private final DashboardController dashboardController;
    private final ClientController clientController;
    private final DietController dietController;
    private final DietGeneratorController dietGeneratorController;
    private final ClientRepository clientRepository;
    private final DietResultRepository dietResultRepository;
    private final BackupService backupService;
    private final TrendController trendController;
    private final AuditLogController auditLogController;
    private final I18nService i18n;
    private final UserContextService userContextService;
    private final LicenseService licenseService;

    @Value("${app.version:dev}")
    private String appVersion;

    public SceneBuilder(StageManager stageManager,
                        DashboardController dashboardController,
                        ClientController clientController,
                        DietController dietController,
                        DietGeneratorController dietGeneratorController,
                        ClientRepository clientRepository,
                        DietResultRepository dietResultRepository,
                        BackupService backupService,
                        TrendController trendController,
                        AuditLogController auditLogController,
                        I18nService i18n,
                        UserContextService userContextService,
                        LicenseService licenseService) {
        this.stageManager = stageManager;
        this.dashboardController = dashboardController;
        this.clientController = clientController;
        this.dietController = dietController;
        this.dietGeneratorController = dietGeneratorController;
        this.clientRepository = clientRepository;
        this.dietResultRepository = dietResultRepository;
        this.backupService = backupService;
        this.trendController = trendController;
        this.auditLogController = auditLogController;
        this.i18n = i18n;
        this.userContextService = userContextService;
        this.licenseService = licenseService;
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
        Label appSub = new Label(i18n.t("app.subtitle"));
        appSub.getStyleClass().add("sidebar-subtitle");
        logoText.getChildren().addAll(appName, appSub);
        logoRow.getChildren().add(logoText);

        logoArea.getChildren().add(logoRow);

        // Nav section
        VBox navContainer = new VBox(2);
        navContainer.setPadding(new Insets(16, 10, 8, 10));

        Label menuLabel = new Label(i18n.t("nav.menu"));
        menuLabel.getStyleClass().add("nav-section-label");

        Button dashBtn   = buildNavItem(i18n.t("nav.dashboard"),    "dashboard".equals(activeScene));
        Button clientBtn = buildNavItem(i18n.t("nav.clients"),       "client".equals(activeScene));
        Button dietBtn   = buildNavItem(i18n.t("nav.diet.history"),  "diet".equals(activeScene));
        Button genBtn    = buildNavItem(i18n.t("nav.diet.generate"), "diet-generator".equals(activeScene));
        Button trendBtn  = buildNavItem(i18n.t("nav.trend"),         "trend".equals(activeScene));
        Button auditBtn  = buildNavItem(i18n.t("nav.audit"),         "audit-log".equals(activeScene));

        dashBtn.setOnAction(e   -> stageManager.switchScene("dashboard"));
        clientBtn.setOnAction(e -> stageManager.switchScene("client"));
        dietBtn.setOnAction(e   -> stageManager.switchScene("diet"));
        genBtn.setOnAction(e    -> stageManager.switchScene("diet-generator"));
        trendBtn.setOnAction(e  -> stageManager.switchScene("trend"));
        auditBtn.setOnAction(e  -> stageManager.switchScene("audit-log"));

        navContainer.getChildren().addAll(menuLabel, dashBtn, clientBtn, dietBtn, genBtn, trendBtn, auditBtn);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom area
        VBox bottomArea = new VBox(6);
        bottomArea.setPadding(new Insets(8, 10, 16, 10));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e293b;");
        sep.setPadding(new Insets(0, 0, 4, 0));

        // Language toggle IT / EN
        HBox langRow = new HBox(4);
        langRow.setAlignment(Pos.CENTER_LEFT);
        langRow.setPadding(new Insets(0, 0, 2, 4));
        Button itBtn = new Button("IT");
        Button enBtn = new Button("EN");
        styleActiveLangBtn(itBtn, Locale.ITALIAN.equals(i18n.getLocale()));
        styleActiveLangBtn(enBtn, Locale.ENGLISH.equals(i18n.getLocale()));
        itBtn.setOnAction(e -> i18n.setLocale(Locale.ITALIAN));
        enBtn.setOnAction(e -> i18n.setLocale(Locale.ENGLISH));
        langRow.getChildren().addAll(itBtn, enBtn);

        Button exitBtn = new Button(i18n.t("nav.exit"));
        exitBtn.getStyleClass().add("nav-item-exit");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setOnAction(e -> System.exit(0));

        Label versionLabel = new Label("v" + appVersion);
        versionLabel.getStyleClass().add("sidebar-subtitle");
        versionLabel.setPadding(new Insets(4, 8, 0, 8));

        LicenseService.Status licStatus = licenseService.getStatus();
        if (licStatus == LicenseService.Status.TRIAL_ACTIVE) {
            long daysLeft = licenseService.getTrialDaysRemaining();
            Label trialBanner = new Label(i18n.t("license.trial.banner", daysLeft));
            trialBanner.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: #f59e0b; -fx-font-weight: bold;" +
                "-fx-padding: 4 8 0 8;"
            );
            bottomArea.getChildren().addAll(sep, langRow, trialBanner, exitBtn, versionLabel);
        } else {
            bottomArea.getChildren().addAll(sep, langRow, exitBtn, versionLabel);
        }

        sidebar.getChildren().addAll(logoArea, navContainer, spacer, bottomArea);
        return sidebar;
    }

    private void styleActiveLangBtn(Button btn, boolean active) {
        btn.setStyle(active
            ? "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4;"
            : "-fx-background-color: #334155; -fx-text-fill: #94a3b8; -fx-padding: 3 8; -fx-background-radius: 4;");
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

        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label(getGreeting());
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("dashboard.subtitle"));
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(24);
        content.getStyleClass().add("content-area");

        var currentUser = userContextService.getCurrentUser();
        long clientCount = safeCount(() -> clientRepository.countByUser(currentUser));
        long dietCount   = safeCount(() -> dietResultRepository.countByUser(currentUser));

        dashClientCountLabel = new Label(String.valueOf(clientCount));
        dashDietCountLabel   = new Label(String.valueOf(dietCount));
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            buildDynamicStatCard(dashClientCountLabel, i18n.t("dashboard.stat.clients"),   i18n.t("dashboard.stat.clients.desc"),   "#6366f1", "#eef2ff"),
            buildDynamicStatCard(dashDietCountLabel,   i18n.t("dashboard.stat.diets"),     i18n.t("dashboard.stat.diets.desc"),     "#10b981", "#ecfdf5"),
            buildStatCard("28",                        i18n.t("dashboard.stat.models"),    i18n.t("dashboard.stat.models.desc"),    "#f59e0b", "#fffbeb"),
            buildStatCard("3",                         i18n.t("dashboard.stat.providers"), i18n.t("dashboard.stat.providers.desc"), "#8b5cf6", "#f5f3ff")
        );

        VBox actionsCard = new VBox(14);
        actionsCard.getStyleClass().add("card");
        Label actionsTitle = new Label(i18n.t("dashboard.actions.title"));
        actionsTitle.getStyleClass().add("card-title");

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button goClients = new Button(i18n.t("dashboard.actions.clients"));
        goClients.getStyleClass().add("btn-primary");
        goClients.setOnAction(e -> stageManager.switchScene("client"));
        Button goDietGen = new Button(i18n.t("dashboard.actions.generate"));
        goDietGen.getStyleClass().add("btn-success");
        goDietGen.setOnAction(e -> stageManager.switchScene("diet-generator"));
        Button goDietList = new Button(i18n.t("dashboard.actions.history"));
        goDietList.getStyleClass().add("btn-info");
        goDietList.setOnAction(e -> stageManager.switchScene("diet"));
        Button backupBtn = new Button(i18n.t("dashboard.actions.backup"));
        backupBtn.getStyleClass().add("btn-warning");
        backupBtn.setOnAction(e -> handleBackup());
        actions.getChildren().addAll(goClients, goDietGen, goDietList, backupBtn);
        actionsCard.getChildren().addAll(actionsTitle, actions);

        HBox infoRow = new HBox(16);
        VBox infoCard1 = buildInfoCard(i18n.t("dashboard.info.start.title"),  i18n.t("dashboard.info.start.text"));
        VBox infoCard2 = buildInfoCard(i18n.t("dashboard.info.models.title"), i18n.t("dashboard.info.models.text"));
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
        var u = userContextService.getCurrentUser();
        if (dashClientCountLabel != null)
            dashClientCountLabel.setText(String.valueOf(safeCount(() -> clientRepository.countByUser(u))));
        if (dashDietCountLabel != null)
            dashDietCountLabel.setText(String.valueOf(safeCount(() -> dietResultRepository.countByUser(u))));
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return i18n.t("greeting.morning");
        if (hour < 18) return i18n.t("greeting.afternoon");
        return i18n.t("greeting.evening");
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
        valueLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");

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

    // ───────────────────────────── AUDIT LOG ─────────────────────────────

    public Scene buildAuditLogScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("audit-log"));

        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label(i18n.t("audit.title"));
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("audit.subtitle"));
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        TableView<AuditLogEntity> table = new TableView<>();
        table.setPlaceholder(new Label(i18n.t("audit.table.empty")));
        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AuditLogEntity, String> tsCol = new TableColumn<>(i18n.t("audit.col.timestamp"));
        tsCol.setPrefWidth(155);
        tsCol.setCellValueFactory(c -> {
            java.time.LocalDateTime ts = c.getValue().getOccurredAt();
            return new SimpleStringProperty(ts != null
                    ? ts.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "");
        });

        TableColumn<AuditLogEntity, String> userCol = new TableColumn<>(i18n.t("audit.col.user"));
        userCol.setPrefWidth(110);
        userCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getUsername() != null ? c.getValue().getUsername() : ""));

        TableColumn<AuditLogEntity, String> actionCol = new TableColumn<>(i18n.t("audit.col.action"));
        actionCol.setPrefWidth(155);
        actionCol.setCellValueFactory(c -> new SimpleStringProperty(
                i18n.t("audit.action." + c.getValue().getAction())));

        TableColumn<AuditLogEntity, String> detailsCol = new TableColumn<>(i18n.t("audit.col.details"));
        detailsCol.setPrefWidth(260);
        detailsCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDetails() != null ? c.getValue().getDetails() : ""));

        TableColumn<AuditLogEntity, String> outcomeCol = new TableColumn<>(i18n.t("audit.col.outcome"));
        outcomeCol.setPrefWidth(75);
        outcomeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOutcome()));
        outcomeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("OK".equals(item)
                        ? "-fx-text-fill: #16a34a; -fx-font-weight: bold;"
                        : "-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            }
        });

        table.getColumns().addAll(tsCol, userCol, actionCol, detailsCol, outcomeCol);
        root.setCenter(table);

        HBox footer = new HBox();
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button refreshBtn = new Button(i18n.t("audit.btn.refresh"));
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> auditLogController.refresh());
        footer.getChildren().add(refreshBtn);
        root.setBottom(footer);

        auditLogController.setup(table);
        stageManager.registerRefresh("audit-log", auditLogController::refresh);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    private void handleBackup() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.t("backup.chooser.title"));
        chooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        java.io.File dir = chooser.showDialog(stageManager.getPrimaryStage());
        if (dir == null) return;
        try {
            Path backupFile = backupService.backup(dir.toPath());
            new Alert(Alert.AlertType.INFORMATION,
                    i18n.t("backup.success", backupFile.toAbsolutePath()),
                    ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    i18n.t("backup.error", ex.getMessage()),
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
        Label title = new Label(i18n.t("clients.title"));
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("clients.subtitle"));
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText(i18n.t("clients.search.prompt"));

        Button addBtn = new Button(i18n.t("clients.btn.new"));
        addBtn.getStyleClass().add("btn-primary");

        header.getChildren().addAll(headerBlock, headerSpacer, searchField, addBtn);
        root.setTop(header);

        // ─── Client table ────────────────────────────────────────────────
        TableView<ClientDto> clientTable = new TableView<>();
        clientTable.getStyleClass().add("styled-table");
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clientTable.setPlaceholder(new Label(i18n.t("clients.table.empty")));

        TableColumn<ClientDto, String> nameCol    = new TableColumn<>(i18n.t("clients.table.name"));
        TableColumn<ClientDto, String> surnameCol = new TableColumn<>(i18n.t("clients.table.surname"));
        TableColumn<ClientDto, String> ageCol     = new TableColumn<>(i18n.t("clients.table.age"));
        TableColumn<ClientDto, String> countryCol = new TableColumn<>(i18n.t("clients.table.country"));

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
        Label historyLabel = new Label(i18n.t("clients.history.placeholder"));
        historyLabel.getStyleClass().add("card-title");

        TableView<AnthropometryDto> historyTable = new TableView<>();
        historyTable.getStyleClass().add("styled-table");
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPlaceholder(new Label(i18n.t("clients.history.empty")));

        TableColumn<AnthropometryDto, String> dateCol = new TableColumn<>(i18n.t("clients.history.date"));
        TableColumn<AnthropometryDto, String> hCol    = new TableColumn<>(i18n.t("clients.history.height"));
        TableColumn<AnthropometryDto, String> wCol    = new TableColumn<>(i18n.t("clients.history.weight"));
        TableColumn<AnthropometryDto, String> bmiCol  = new TableColumn<>(i18n.t("clients.history.bmi"));
        TableColumn<AnthropometryDto, String> plCol   = new TableColumn<>(i18n.t("clients.history.fold"));
        TableColumn<AnthropometryDto, String> cirCol  = new TableColumn<>(i18n.t("clients.history.circumference"));

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
        plCol.setCellValueFactory(cd  -> new SimpleStringProperty(
                cd.getValue().getFold()          != null ? i18n.t("common.yes") : "-"));
        cirCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCircumference() != null ? i18n.t("common.yes") : "-"));

        historyTable.getColumns().addAll(dateCol, hCol, wCol, bmiCol, plCol, cirCol);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        VBox historyPanel = new VBox(8, historyLabel, historyTable);
        historyPanel.getStyleClass().add("content-area");
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        // ─── Anthropometric detail pane ──────────────────────────────────
        VBox visitDetailBox = new VBox(12);
        visitDetailBox.setPadding(new Insets(16));
        visitDetailBox.setStyle("-fx-background-color: white;");
        Label detailPlaceholder = new Label(i18n.t("clients.visit.placeholder"));
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

        Button exportExcelBtn = new Button(i18n.t("clients.btn.export"));
        exportExcelBtn.getStyleClass().add("btn-secondary");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button newVisitBtn = new Button(i18n.t("clients.btn.new.visit"));
        newVisitBtn.getStyleClass().add("btn-success");
        Button editBtn   = new Button(i18n.t("clients.btn.edit"));
        editBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button(i18n.t("clients.btn.delete"));
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
        Label title = new Label(i18n.t("diet.title"));
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("diet.subtitle"));
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button genBtn = new Button(i18n.t("diet.btn.generate"));
        genBtn.getStyleClass().add("btn-success");
        genBtn.setOnAction(e -> stageManager.switchScene("diet-generator"));

        header.getChildren().addAll(headerBlock, headerSpacer, genBtn);
        root.setTop(header);

        TableView<DietResultEntity> dietTable = new TableView<>();
        dietTable.getStyleClass().add("styled-table");
        dietTable.setPlaceholder(new Label(i18n.t("diet.table.empty")));

        TextField searchField = new TextField();
        searchField.setPromptText(i18n.t("diet.search.prompt"));
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
        Button viewBtn   = new Button(i18n.t("diet.btn.view"));
        viewBtn.getStyleClass().add("btn-info");
        Button deleteBtn = new Button(i18n.t("diet.btn.delete"));
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
        Label title = new Label(i18n.t("dietgen.title"));
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("dietgen.subtitle"));
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
        Label patientTitle = new Label(i18n.t("dietgen.card.patient"));
        patientTitle.getStyleClass().add("card-title");
        Label clientLabel = new Label(i18n.t("dietgen.label.client"));
        clientLabel.getStyleClass().add("form-label");
        ComboBox<String> clientCombo = new ComboBox<>();
        clientCombo.getStyleClass().add("form-combo");
        clientCombo.setMaxWidth(Double.MAX_VALUE);
        clientCombo.setPromptText(i18n.t("dietgen.combo.client.prompt"));
        patientCard.getChildren().addAll(patientTitle, clientLabel, clientCombo);

        // Card AI
        VBox aiCard = new VBox(10);
        aiCard.getStyleClass().add("card");
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        Label aiTitle = new Label(i18n.t("dietgen.card.ai"));
        aiTitle.getStyleClass().add("card-title");

        Label providerLabel = new Label(i18n.t("dietgen.label.provider"));
        providerLabel.getStyleClass().add("form-label");
        ComboBox<String> providerCombo = new ComboBox<>();
        providerCombo.getStyleClass().add("form-combo");
        providerCombo.setMaxWidth(Double.MAX_VALUE);

        Label modelLabel = new Label(i18n.t("dietgen.label.model"));
        modelLabel.getStyleClass().add("form-label");
        ComboBox<String> aiModelCombo = new ComboBox<>();
        aiModelCombo.getStyleClass().add("form-combo");
        aiModelCombo.setMaxWidth(Double.MAX_VALUE);
        aiModelCombo.setPromptText(i18n.t("dietgen.combo.model.prompt"));

        HBox credRow = new HBox(10);
        credRow.setAlignment(Pos.CENTER_LEFT);
        Label credentialStatusLabel = new Label(i18n.t("dietgen.cred.missing"));
        credentialStatusLabel.getStyleClass().add("credential-missing");
        Region credSpacer = new Region();
        HBox.setHgrow(credSpacer, Priority.ALWAYS);
        Button configureButton = new Button(i18n.t("dietgen.btn.configure"));
        configureButton.getStyleClass().add("btn-configure");
        credRow.getChildren().addAll(credentialStatusLabel, credSpacer, configureButton);

        aiCard.getChildren().addAll(aiTitle, providerLabel, providerCombo, modelLabel, aiModelCombo, credRow);
        row1.getChildren().addAll(patientCard, aiCard);

        // Card Parametri
        VBox paramsCard = new VBox(14);
        paramsCard.getStyleClass().add("card");
        Label paramsTitle = new Label(i18n.t("dietgen.card.params"));
        paramsTitle.getStyleClass().add("card-title");

        HBox paramsRow = new HBox(20);
        paramsRow.setAlignment(Pos.TOP_LEFT);

        VBox goalGroup = new VBox(6);
        HBox.setHgrow(goalGroup, Priority.ALWAYS);
        Label goalLabel = new Label(i18n.t("dietgen.label.goal"));
        goalLabel.getStyleClass().add("form-label");
        ComboBox<String> goalBox = new ComboBox<>();
        goalBox.getItems().addAll(
            i18n.t("dietgen.goal.weight.loss"),
            i18n.t("dietgen.goal.muscle"),
            i18n.t("dietgen.goal.maintain"),
            i18n.t("dietgen.goal.health"),
            i18n.t("dietgen.goal.performance")
        );
        goalBox.setValue(i18n.t("dietgen.goal.maintain"));
        goalBox.getStyleClass().add("form-combo");
        goalBox.setMaxWidth(Double.MAX_VALUE);
        goalGroup.getChildren().addAll(goalLabel, goalBox);

        VBox prefGroup = new VBox(6);
        HBox.setHgrow(prefGroup, Priority.ALWAYS);
        Label prefLabel = new Label(i18n.t("dietgen.label.pref"));
        prefLabel.getStyleClass().add("form-label");
        ComboBox<String> prefBox = new ComboBox<>();
        prefBox.getItems().addAll(
            i18n.t("dietgen.pref.omnivore"),
            i18n.t("dietgen.pref.vegetarian"),
            i18n.t("dietgen.pref.vegan"),
            i18n.t("dietgen.pref.gluten.free"),
            i18n.t("dietgen.pref.lactose.free"),
            i18n.t("dietgen.pref.keto")
        );
        prefBox.setValue(i18n.t("dietgen.pref.omnivore"));
        prefBox.getStyleClass().add("form-combo");
        prefBox.setMaxWidth(Double.MAX_VALUE);
        prefGroup.getChildren().addAll(prefLabel, prefBox);

        VBox activityGroup = new VBox(6);
        HBox.setHgrow(activityGroup, Priority.ALWAYS);
        Label activityLabel = new Label(i18n.t("dietgen.label.activity"));
        activityLabel.getStyleClass().add("form-label");
        ComboBox<String> activityBox = new ComboBox<>();
        activityBox.getItems().addAll(
            i18n.t("dietgen.activity.sedentary"),
            i18n.t("dietgen.activity.light"),
            i18n.t("dietgen.activity.moderate"),
            i18n.t("dietgen.activity.active"),
            i18n.t("dietgen.activity.athlete")
        );
        activityBox.setValue(i18n.t("dietgen.activity.moderate"));
        activityBox.getStyleClass().add("form-combo");
        activityBox.setMaxWidth(Double.MAX_VALUE);
        activityGroup.getChildren().addAll(activityLabel, activityBox);

        paramsRow.getChildren().addAll(goalGroup, prefGroup, activityGroup);
        paramsCard.getChildren().addAll(paramsTitle, paramsRow);

        // Card Pasto Libero / Sgarro
        VBox freeCard = new VBox(12);
        freeCard.getStyleClass().add("card");
        Label freeTitle = new Label(i18n.t("dietgen.free.card.title"));
        freeTitle.getStyleClass().add("card-title");
        Label freeSubtitle = new Label(i18n.t("dietgen.free.card.subtitle"));
        freeSubtitle.getStyleClass().add("card-subtitle");

        Label daysLabel = new Label(i18n.t("dietgen.free.days.label"));
        daysLabel.getStyleClass().add("form-label");
        List<CheckBox> dayCheckBoxes = new ArrayList<>();
        FlowPane daysRow = new FlowPane(16, 10);
        daysRow.setAlignment(Pos.CENTER_LEFT);
        for (String key : new String[]{
                "dietgen.free.day.mon", "dietgen.free.day.tue", "dietgen.free.day.wed",
                "dietgen.free.day.thu", "dietgen.free.day.fri", "dietgen.free.day.sat",
                "dietgen.free.day.sun"}) {
            CheckBox cb = new CheckBox(i18n.t(key));
            cb.getStyleClass().add("free-meal-check");
            dayCheckBoxes.add(cb);
            daysRow.getChildren().add(cb);
        }

        Label mealsLabel = new Label(i18n.t("dietgen.free.meals.label"));
        mealsLabel.getStyleClass().add("form-label");
        List<CheckBox> mealCheckBoxes = new ArrayList<>();
        FlowPane mealsRow = new FlowPane(24, 10);
        mealsRow.setAlignment(Pos.CENTER_LEFT);
        for (String key : new String[]{
                "dietgen.free.meal.breakfast", "dietgen.free.meal.lunch", "dietgen.free.meal.dinner",
                "dietgen.free.meal.snack", "dietgen.free.meal.afternoon"}) {
            CheckBox cb = new CheckBox(i18n.t(key));
            cb.getStyleClass().add("free-meal-check");
            mealCheckBoxes.add(cb);
            mealsRow.getChildren().add(cb);
        }
        freeCard.getChildren().addAll(freeTitle, freeSubtitle, daysLabel, daysRow, mealsLabel, mealsRow);

        // Card Note
        VBox notesCard = new VBox(10);
        notesCard.getStyleClass().add("card");
        Label notesTitle = new Label(i18n.t("dietgen.card.notes"));
        notesTitle.getStyleClass().add("card-title");
        Label notesSubtitle = new Label(i18n.t("dietgen.notes.subtitle"));
        notesSubtitle.getStyleClass().add("card-subtitle");
        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("form-field");
        notesArea.setPrefHeight(90);
        notesArea.setWrapText(true);
        notesArea.setPromptText(i18n.t("dietgen.notes.prompt"));
        notesCard.getChildren().addAll(notesTitle, notesSubtitle, notesArea);

        content.getChildren().addAll(row1, paramsCard, freeCard, notesCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        HBox footer = new HBox(12);
        footer.getStyleClass().add("scene-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(26, 26);
        progressIndicator.setVisible(false);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button generateBtn = new Button(i18n.t("dietgen.btn.generate"));
        generateBtn.getStyleClass().add("btn-success");

        footer.getChildren().addAll(progressIndicator, footerSpacer, generateBtn);
        root.setBottom(footer);

        dietGeneratorController.setup(providerCombo, aiModelCombo, clientCombo,
                generateBtn, configureButton, credentialStatusLabel, progressIndicator,
                goalBox, prefBox, activityBox, notesArea, dayCheckBoxes, mealCheckBoxes);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    // ───────────────────────────── TREND ─────────────────────────────

    public Scene buildTrendScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");
        root.setLeft(buildSidebar("trend"));

        HBox header = new HBox();
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerBlock = new VBox(2);
        Label title = new Label(i18n.t("trend.title"));
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(i18n.t("trend.subtitle"));
        subtitle.getStyleClass().add("page-subtitle");
        headerBlock.getChildren().addAll(title, subtitle);
        header.getChildren().add(headerBlock);
        root.setTop(header);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        // ─── Selector card ────────────────────────────────────────────
        VBox selectorCard = new VBox(10);
        selectorCard.getStyleClass().add("card");
        Label selectorTitle = new Label(i18n.t("trend.card.selector"));
        selectorTitle.getStyleClass().add("card-title");

        HBox selectorRow = new HBox(16);
        selectorRow.setAlignment(Pos.CENTER_LEFT);
        ComboBox<ClientDto> clientCombo = new ComboBox<>();
        clientCombo.getStyleClass().add("form-combo");
        clientCombo.setPromptText(i18n.t("trend.combo.prompt"));
        clientCombo.setPrefWidth(300);
        selectorRow.getChildren().add(clientCombo);
        selectorCard.getChildren().addAll(selectorTitle, selectorRow);

        // ─── Stat cards row ───────────────────────────────────────────
        Label weightStatVal    = new Label("—");
        Label bmiStatVal       = new Label("—");
        Label deltaStatVal     = new Label("—");
        Label lastVisitStatVal = new Label("—");

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            buildTrendStatCard(weightStatVal,    i18n.t("trend.stat.weight"),    i18n.t("trend.stat.weight.desc"),    "#6366f1", "#eef2ff"),
            buildTrendStatCard(bmiStatVal,       i18n.t("trend.stat.bmi"),       i18n.t("trend.stat.bmi.desc"),       "#10b981", "#ecfdf5"),
            buildTrendStatCard(deltaStatVal,     i18n.t("trend.stat.delta"),     i18n.t("trend.stat.delta.desc"),     "#f59e0b", "#fffbeb"),
            buildTrendStatCard(lastVisitStatVal, i18n.t("trend.stat.lastvisit"), i18n.t("trend.stat.lastvisit.desc"), "#8b5cf6", "#f5f3ff")
        );

        // ─── Placeholder (no data) ────────────────────────────────────
        VBox noDataBox = new VBox(12);
        noDataBox.setAlignment(Pos.CENTER);
        noDataBox.setPrefHeight(300);
        noDataBox.getStyleClass().add("card");
        Label noDataIcon = new Label("📊");
        noDataIcon.setStyle("-fx-font-size: 40px;");
        Label noDataLabel = new Label(i18n.t("trend.no.data"));
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        Label noDataHint = new Label(i18n.t("trend.no.data.hint"));
        noDataHint.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        noDataBox.getChildren().addAll(noDataIcon, noDataLabel, noDataHint);

        // ─── Charts ───────────────────────────────────────────────────
        VBox chartsBox = new VBox(16);
        chartsBox.setVisible(false);
        chartsBox.setManaged(false);

        LineChart<String, Number> weightChart = buildLineChart(
                i18n.t("trend.chart.weight"), i18n.t("trend.axis.visit"), i18n.t("trend.axis.weight"));
        weightChart.getStyleClass().add("weight-chart");
        LineChart<String, Number> bmiChart = buildLineChart(
                i18n.t("trend.chart.bmi"), i18n.t("trend.axis.visit"), i18n.t("trend.axis.bmi"));
        bmiChart.getStyleClass().add("bmi-chart");

        VBox weightCard = new VBox(weightChart);
        weightCard.getStyleClass().add("chart-card");
        VBox bmiCard = new VBox(bmiChart);
        bmiCard.getStyleClass().add("chart-card");

        HBox chartsRow = new HBox(16);
        HBox.setHgrow(weightCard, Priority.ALWAYS);
        HBox.setHgrow(bmiCard,    Priority.ALWAYS);
        chartsRow.getChildren().addAll(weightCard, bmiCard);
        chartsBox.getChildren().add(chartsRow);

        // ─── BMI reference legend ─────────────────────────────────────
        HBox bmiLegend = new HBox(20);
        bmiLegend.setAlignment(Pos.CENTER_LEFT);
        bmiLegend.setStyle("-fx-padding: 0 0 0 4;");
        bmiLegend.getChildren().addAll(
            bmiTag("< 18.5",    i18n.t("trend.bmi.underweight"), "#3b82f6"),
            bmiTag("18.5–24.9", i18n.t("trend.bmi.normal"),      "#10b981"),
            bmiTag("25–29.9",   i18n.t("trend.bmi.overweight"),  "#f59e0b"),
            bmiTag(">= 30",     i18n.t("trend.bmi.obese"),       "#ef4444")
        );
        chartsBox.getChildren().add(bmiLegend);

        content.getChildren().addAll(selectorCard, statsRow, noDataBox, chartsBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        trendController.setup(clientCombo, weightChart, bmiChart, noDataBox, chartsBox,
                              weightStatVal, bmiStatVal, deltaStatVal, lastVisitStatVal);
        stageManager.registerRefresh("trend", trendController::refresh);

        Scene scene = new Scene(root, 1100, 720);
        addStyles(scene);
        return scene;
    }

    private VBox buildTrendStatCard(Label valueLabel, String label, String desc,
                                    String accentColor, String bgColor) {
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");
        VBox card = new VBox(6);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 18 20 18 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        Label labelEl = new Label(label);
        labelEl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label descEl = new Label(desc);
        descEl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        card.getChildren().addAll(valueLabel, labelEl, descEl);
        return card;
    }

    private LineChart<String, Number> buildLineChart(String title, String xLabel, String yLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);
        yAxis.setForceZeroInRange(false);
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(false);
        chart.setPrefHeight(320);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return chart;
    }

    private Label bmiTag(String range, String label, String color) {
        Label l = new Label(range + "  " + label);
        l.setStyle(
            "-fx-background-color: " + color + "22;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 4; -fx-background-radius: 4;" +
            "-fx-padding: 3 8 3 8; -fx-font-size: 11px; -fx-text-fill: " + color + ";"
        );
        return l;
    }
}
