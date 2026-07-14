package com.angeloni.nutricare.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

/**
 * Manages scene transitions and navigation in the JavaFX application.
 */
@Component
public class StageManager {

    private Stage stage;
    private Scene dashboardScene;
    private Scene clientScene;
    private Scene dietScene;
    private Scene dietGeneratorScene;

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void switchScene(String sceneName) {
        switch (sceneName) {
            case "dashboard" -> stage.setScene(dashboardScene);
            case "client" -> stage.setScene(clientScene);
            case "diet" -> stage.setScene(dietScene);
            case "diet-generator" -> stage.setScene(dietGeneratorScene);
            default -> throw new IllegalArgumentException("Scene not found: " + sceneName);
        }
    }

    public void setDashboardScene(Scene scene) {
        this.dashboardScene = scene;
    }

    public void setClientScene(Scene scene) {
        this.clientScene = scene;
    }

    public void setDietScene(Scene scene) {
        this.dietScene = scene;
    }

    public void setDietGeneratorScene(Scene scene) {
        this.dietGeneratorScene = scene;
    }

    public Stage getPrimaryStage() {
        return stage;
    }
}

