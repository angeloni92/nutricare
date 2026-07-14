package com.angeloni.nutricare.ui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.springframework.stereotype.Component;
import com.angeloni.nutricare.ui.controller.*;

/**
 * Builds and creates JavaFX scenes programmatically
 */
@Component
public class SceneBuilder {

    private final LoginController loginController;
    private final DashboardController dashboardController;
    private final ClientController clientController;
    private final DietController dietController;
    private final DietGeneratorController dietGeneratorController;

    public SceneBuilder(LoginController loginController, DashboardController dashboardController,
                       ClientController clientController, DietController dietController,
                       DietGeneratorController dietGeneratorController) {
        this.loginController = loginController;
        this.dashboardController = dashboardController;
        this.clientController = clientController;
        this.dietController = dietController;
        this.dietGeneratorController = dietGeneratorController;
    }

    /**
     * Create the login scene
     */
    public Scene buildLoginScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40; -fx-background-color: #f5f5f5;");

        // Title
        Label title = new Label("Nutricare");
        title.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Nutrition Management System");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");

        // Form
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: white;");
        formBox.setMaxWidth(400);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(150);
        loginButton.setStyle("""
            -fx-font-size: 14;
            -fx-padding: 10;
            -fx-background-color: #007bff;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(150);
        registerButton.setStyle("""
            -fx-font-size: 14;
            -fx-padding: 10;
            -fx-background-color: #28a745;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        formBox.getChildren().addAll(
            emailLabel, emailField,
            passwordLabel, passwordField,
            buttonBox, errorLabel
        );

        root.getChildren().addAll(title, subtitle, formBox);

        return new Scene(root, 800, 600);
    }

    /**
     * Create the dashboard scene
     */
    public Scene buildDashboardScene() {
        BorderPane root = new BorderPane();

        // Sidebar
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // Center content
        VBox centerContent = new VBox(20);
        centerContent.setStyle("-fx-padding: 20; -fx-background-color: #ecf0f1;");
        centerContent.setAlignment(Pos.TOP_CENTER);

        Label welcome = new Label("Welcome to Nutricare");
        welcome.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label description = new Label("Select an option from the menu to get started");
        description.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");

        centerContent.getChildren().addAll(welcome, description);
        root.setCenter(centerContent);

        return new Scene(root, 1000, 700);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setStyle("""
            -fx-background-color: #2c3e50;
            -fx-padding: 20;
        """);
        sidebar.setMinWidth(200);

        Label appName = new Label("NUTRICARE");
        appName.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");

        Button clientsButton = new Button("👥 Clients");
        clientsButton.setPrefWidth(150);
        Button dietButton = new Button("🍽️ Diets");
        dietButton.setPrefWidth(150);
        Button dietGenButton = new Button("🤖 Generate Diet");
        dietGenButton.setPrefWidth(150);

        String buttonStyle = """
            -fx-font-size: 13;
            -fx-padding: 12;
            -fx-background-color: #34495e;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """;

        clientsButton.setStyle(buttonStyle);
        dietButton.setStyle(buttonStyle);
        dietGenButton.setStyle(buttonStyle);

        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 10;");

        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setPrefWidth(150);
        logoutButton.setStyle("""
            -fx-font-size: 11;
            -fx-padding: 8;
            -fx-background-color: #e74c3c;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        sidebar.getChildren().addAll(
            appName, new Separator(),
            clientsButton, dietButton, dietGenButton,
            spacer, separator, logoutButton
        );

        return sidebar;
    }

    /**
     * Create the client management scene
     */
    public Scene buildClientScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        HBox header = new HBox(10);
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Client Management");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search clients...");
        searchField.setPrefWidth(300);

        header.getChildren().addAll(title, searchField);
        root.setTop(header);

        // Table
        TableView<String> clientTable = new TableView<>();
        TableColumn<String, String> nameCol = new TableColumn<>("Name");
        TableColumn<String, String> emailCol = new TableColumn<>("Email");
        TableColumn<String, String> phoneCol = new TableColumn<>("Phone");

        clientTable.getColumns().addAll(nameCol, emailCol, phoneCol);
        root.setCenter(clientTable);

        // Bottom buttons
        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button addBtn = new Button("Add Client");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Back");

        String buttonStyle = """
            -fx-font-size: 12;
            -fx-padding: 8;
            -fx-cursor: hand;
        """;

        addBtn.setStyle(buttonStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");
        editBtn.setStyle(buttonStyle + "-fx-background-color: #007bff; -fx-text-fill: white;");
        deleteBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");
        backBtn.setStyle(buttonStyle + "-fx-background-color: #6c757d; -fx-text-fill: white;");

        buttons.getChildren().addAll(addBtn, editBtn, deleteBtn, backBtn);
        root.setBottom(buttons);

        return new Scene(root, 1000, 700);
    }

    /**
     * Create the diet management scene
     */
    public Scene buildDietScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header with filter
        HBox header = new HBox(10);
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Diet Management");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("All", "Active", "Completed");
        filterBox.setValue("All");

        header.getChildren().addAll(title, new Separator(javafx.geometry.Orientation.VERTICAL), filterBox);
        root.setTop(header);

        // Table
        TableView<String> dietTable = new TableView<>();
        TableColumn<String, String> clientCol = new TableColumn<>("Client");
        TableColumn<String, String> dateCol = new TableColumn<>("Date");
        TableColumn<String, String> statusCol = new TableColumn<>("Status");

        dietTable.getColumns().addAll(clientCol, dateCol, statusCol);
        root.setCenter(dietTable);

        // Bottom buttons
        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("View");
        Button generateBtn = new Button("Generate");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Back");

        String buttonStyle = """
            -fx-font-size: 12;
            -fx-padding: 8;
            -fx-cursor: hand;
        """;

        viewBtn.setStyle(buttonStyle + "-fx-background-color: #007bff; -fx-text-fill: white;");
        generateBtn.setStyle(buttonStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");
        deleteBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");
        backBtn.setStyle(buttonStyle + "-fx-background-color: #6c757d; -fx-text-fill: white;");

        buttons.getChildren().addAll(viewBtn, generateBtn, deleteBtn, backBtn);
        root.setBottom(buttons);

        return new Scene(root, 1000, 700);
    }

    /**
     * Create the diet generator scene
     */
    public Scene buildDietGeneratorScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        HBox header = new HBox();
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        Label title = new Label("🤖 AI Diet Generator");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        header.getChildren().add(title);
        root.setTop(header);

        // Form
        VBox form = new VBox(15);
        form.setStyle("-fx-padding: 20;");

        Label clientLabel = new Label("Select Client:");
        ComboBox<String> clientBox = new ComboBox<>();
        clientBox.setPrefWidth(300);

        Label modelLabel = new Label("AI Model:");
        ComboBox<String> modelBox = new ComboBox<>();
        modelBox.getItems().addAll("GPT-4", "GPT-3.5", "Claude");
        modelBox.setPrefWidth(300);

        Label goalLabel = new Label("Primary Goal:");
        ComboBox<String> goalBox = new ComboBox<>();
        goalBox.getItems().addAll("Weight Loss", "Muscle Gain", "Maintenance");
        goalBox.setPrefWidth(300);

        Label prefLabel = new Label("Dietary Preference:");
        ComboBox<String> prefBox = new ComboBox<>();
        prefBox.getItems().addAll("Omnivore", "Vegetarian", "Vegan");
        prefBox.setPrefWidth(300);

        Label notesLabel = new Label("Additional Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPrefHeight(100);
        notesArea.setWrapText(true);

        Button generateBtn = new Button("Generate Diet");
        generateBtn.setStyle("""
            -fx-font-size: 14;
            -fx-padding: 10;
            -fx-background-color: #28a745;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);

        form.getChildren().addAll(
            clientLabel, clientBox,
            modelLabel, modelBox,
            goalLabel, goalBox,
            prefLabel, prefBox,
            notesLabel, notesArea,
            generateBtn
        );

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Back button
        HBox buttons = new HBox();
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button backBtn = new Button("Back");
        backBtn.setStyle("""
            -fx-font-size: 12;
            -fx-padding: 8;
            -fx-background-color: #6c757d;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);
        buttons.getChildren().add(backBtn);
        root.setBottom(buttons);

        return new Scene(root, 900, 800);
    }
}

