package com.angeloni.nutricare.ui;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@Component
public class StageManager {

    private Stage stage;
    private Scene dashboardScene;
    private Scene clientScene;
    private Scene dietScene;
    private Scene dietGeneratorScene;
    private Scene trendScene;

    private final Map<String, Runnable> refreshCallbacks = new HashMap<>();

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
        try {
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            stage.getIcons().add(icon);
            stage.setTitle("NutriCare — Nutrition Management");
        } catch (Exception ignored) {}
    }

    public void registerRefresh(String sceneName, Runnable callback) {
        refreshCallbacks.put(sceneName, callback);
    }

    public void refreshScene(String sceneName) {
        Runnable cb = refreshCallbacks.get(sceneName);
        if (cb == null) return;
        if (Platform.isFxApplicationThread()) cb.run();
        else Platform.runLater(cb);
    }

    public void switchScene(String sceneName) {
        switch (sceneName) {
            case "dashboard" -> { stage.setScene(dashboardScene); refreshScene("dashboard"); }
            case "client"    -> stage.setScene(clientScene);
            case "diet"      -> { stage.setScene(dietScene); refreshScene("diet"); }
            case "diet-generator" -> stage.setScene(dietGeneratorScene);
            case "trend"         -> { stage.setScene(trendScene); refreshScene("trend"); }
            default -> throw new IllegalArgumentException("Scene not found: " + sceneName);
        }
    }

    public void setDashboardScene(Scene scene)      { this.dashboardScene      = scene; }
    public void setClientScene(Scene scene)         { this.clientScene         = scene; }
    public void setDietScene(Scene scene)           { this.dietScene           = scene; }
    public void setDietGeneratorScene(Scene scene)  { this.dietGeneratorScene  = scene; }
    public void setTrendScene(Scene scene)          { this.trendScene          = scene; }

    public Stage getPrimaryStage() { return stage; }
}
