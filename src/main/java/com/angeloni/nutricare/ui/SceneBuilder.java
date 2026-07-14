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

    private final DashboardController dashboardController;
    private final ClientController clientController;
    private final DietController dietController;
    private final DietGeneratorController dietGeneratorController;

    public SceneBuilder(DashboardController dashboardController,
                        ClientController clientController, DietController dietController,
                        DietGeneratorController dietGeneratorController) {
        this.dashboardController = dashboardController;
        this.clientController = clientController;
        this.dietController = dietController;
        this.dietGeneratorController = dietGeneratorController;
    }

    /**
     * Create the dashboard scene
     */
    public Scene buildDashboardScene() {
        BorderPane root = new BorderPane();

        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        VBox centerContent = new VBox(20);
        centerContent.setStyle("-fx-padding: 20; -fx-background-color: #ecf0f1;");
        centerContent.setAlignment(Pos.TOP_CENTER);

        Label welcome = new Label("Benvenuto in Nutricare");
        welcome.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label description = new Label("Seleziona un'opzione dal menu per iniziare");
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

        Button clientsButton = new Button("Clienti");
        clientsButton.setPrefWidth(150);
        Button dietButton = new Button("Diete");
        dietButton.setPrefWidth(150);
        Button dietGenButton = new Button("Genera Dieta AI");
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

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button exitButton = new Button("Esci");
        exitButton.setPrefWidth(150);
        exitButton.setStyle("""
            -fx-font-size: 11;
            -fx-padding: 8;
            -fx-background-color: #e74c3c;
            -fx-text-fill: white;
            -fx-cursor: hand;
        """);
        exitButton.setOnAction(e -> System.exit(0));

        sidebar.getChildren().addAll(
            appName, new Separator(),
            clientsButton, dietButton, dietGenButton,
            spacer, new Separator(), exitButton
        );

        return sidebar;
    }

    /**
     * Create the client management scene
     */
    public Scene buildClientScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        HBox header = new HBox(10);
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestione Clienti");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Cerca clienti...");
        searchField.setPrefWidth(300);

        header.getChildren().addAll(title, searchField);
        root.setTop(header);

        TableView<String> clientTable = new TableView<>();
        TableColumn<String, String> nameCol = new TableColumn<>("Nome");
        TableColumn<String, String> surnameCol = new TableColumn<>("Cognome");
        TableColumn<String, String> ageCol = new TableColumn<>("Età");

        clientTable.getColumns().addAll(nameCol, surnameCol, ageCol);
        root.setCenter(clientTable);

        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);

        String buttonStyle = "-fx-font-size: 12; -fx-padding: 8; -fx-cursor: hand;";

        Button addBtn = new Button("Aggiungi");
        addBtn.setStyle(buttonStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");
        Button editBtn = new Button("Modifica");
        editBtn.setStyle(buttonStyle + "-fx-background-color: #007bff; -fx-text-fill: white;");
        Button deleteBtn = new Button("Elimina");
        deleteBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");
        Button backBtn = new Button("Indietro");
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

        HBox header = new HBox(10);
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestione Diete");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("Tutte", "Attive", "Completate");
        filterBox.setValue("Tutte");

        header.getChildren().addAll(title, new Separator(javafx.geometry.Orientation.VERTICAL), filterBox);
        root.setTop(header);

        TableView<String> dietTable = new TableView<>();
        TableColumn<String, String> clientCol = new TableColumn<>("Cliente");
        TableColumn<String, String> dateCol = new TableColumn<>("Data");
        TableColumn<String, String> modelCol = new TableColumn<>("Modello AI");

        dietTable.getColumns().addAll(clientCol, dateCol, modelCol);
        root.setCenter(dietTable);

        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);

        String buttonStyle = "-fx-font-size: 12; -fx-padding: 8; -fx-cursor: hand;";

        Button viewBtn = new Button("Visualizza");
        viewBtn.setStyle(buttonStyle + "-fx-background-color: #007bff; -fx-text-fill: white;");
        Button generateBtn = new Button("Genera");
        generateBtn.setStyle(buttonStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");
        Button deleteBtn = new Button("Elimina");
        deleteBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");
        Button backBtn = new Button("Indietro");
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

        HBox header = new HBox();
        header.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        Label title = new Label("Generatore Dieta AI");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        header.getChildren().add(title);
        root.setTop(header);

        VBox form = new VBox(15);
        form.setStyle("-fx-padding: 20;");

        Label clientLabel = new Label("Seleziona Cliente:");
        ComboBox<String> clientBox = new ComboBox<>();
        clientBox.setPrefWidth(300);

        Label modelLabel = new Label("Modello AI:");
        ComboBox<String> modelBox = new ComboBox<>();
        modelBox.getItems().addAll("GPT-4", "GPT-3.5", "Claude 3 Sonnet", "Claude 3.5 Sonnet", "Copilot GPT-4o");
        modelBox.setPrefWidth(300);

        Label goalLabel = new Label("Obiettivo Principale:");
        ComboBox<String> goalBox = new ComboBox<>();
        goalBox.getItems().addAll("Perdita Peso", "Aumento Massa", "Mantenimento");
        goalBox.setPrefWidth(300);

        Label prefLabel = new Label("Preferenza Dietetica:");
        ComboBox<String> prefBox = new ComboBox<>();
        prefBox.getItems().addAll("Onnivoro", "Vegetariano", "Vegano");
        prefBox.setPrefWidth(300);

        Label notesLabel = new Label("Note aggiuntive:");
        TextArea notesArea = new TextArea();
        notesArea.setPrefHeight(100);
        notesArea.setWrapText(true);

        Button generateBtn = new Button("Genera Dieta");
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

        HBox buttons = new HBox();
        buttons.setStyle("-fx-padding: 15;");
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button backBtn = new Button("Indietro");
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
