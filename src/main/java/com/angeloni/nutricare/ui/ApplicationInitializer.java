package com.angeloni.nutricare.ui;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.angeloni.nutricare.NutricareApplication;
import javafx.stage.Stage;
import javafx.application.Platform;

/**
 * Coordinates Spring Boot and JavaFX lifecycle
 */
@Component
public class ApplicationInitializer {

    private final StageManager stageManager;
    private final SceneBuilder sceneBuilder;

    public ApplicationInitializer(StageManager stageManager, SceneBuilder sceneBuilder) {
        this.stageManager = stageManager;
        this.sceneBuilder = sceneBuilder;
    }

    /**
     * Called when Spring Boot application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        Platform.runLater(this::initializeUI);
    }

    private void initializeUI() {
        try {
            // Wire the JavaFX primary stage (created before Spring Boot started)
            stageManager.setPrimaryStage(NutricareApplication.primaryStage);

            // Build all scenes
            stageManager.setDashboardScene(sceneBuilder.buildDashboardScene());
            stageManager.setClientScene(sceneBuilder.buildClientScene());
            stageManager.setDietScene(sceneBuilder.buildDietScene());
            stageManager.setDietGeneratorScene(sceneBuilder.buildDietGeneratorScene());

            // Start directly with dashboard (desktop app, no login required)
            stageManager.switchScene("dashboard");

            // Configure window
            Stage primaryStage = stageManager.getPrimaryStage();
            primaryStage.setTitle("Nutricare - Nutrition Management System");
            primaryStage.setWidth(1100);
            primaryStage.setHeight(720);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();

            // Setup event handlers
            primaryStage.setOnCloseRequest(e -> handleWindowClose());

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void handleWindowClose() {
        // TODO: Log out user if necessary
        System.exit(0);
    }
}

