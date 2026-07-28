package com.angeloni.nutricare.ui;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.angeloni.nutricare.NutricareApplication;
import com.angeloni.nutricare.event.LocaleChangedEvent;
import com.angeloni.nutricare.service.I18nService;
import javafx.stage.Stage;
import javafx.application.Platform;

@Component
public class ApplicationInitializer {

    private final StageManager stageManager;
    private final SceneBuilder sceneBuilder;
    private final I18nService i18nService;

    public ApplicationInitializer(StageManager stageManager, SceneBuilder sceneBuilder,
                                  I18nService i18nService) {
        this.stageManager = stageManager;
        this.sceneBuilder = sceneBuilder;
        this.i18nService = i18nService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        Platform.runLater(this::initializeUI);
    }

    @EventListener(LocaleChangedEvent.class)
    public void onLocaleChanged(LocaleChangedEvent event) {
        Platform.runLater(this::rebuildScenes);
    }

    private void initializeUI() {
        try {
            stageManager.setPrimaryStage(NutricareApplication.primaryStage);

            stageManager.setDashboardScene(sceneBuilder.buildDashboardScene());
            stageManager.setClientScene(sceneBuilder.buildClientScene());
            stageManager.setDietScene(sceneBuilder.buildDietScene());
            stageManager.setDietGeneratorScene(sceneBuilder.buildDietGeneratorScene());
            stageManager.setTrendScene(sceneBuilder.buildTrendScene());

            stageManager.switchScene("dashboard");

            Stage primaryStage = stageManager.getPrimaryStage();
            primaryStage.setTitle("Nutricare - Nutrition Management System");
            primaryStage.setWidth(1100);
            primaryStage.setHeight(720);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();

            primaryStage.setOnCloseRequest(e -> System.exit(0));

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void rebuildScenes() {
        String current = stageManager.getCurrentSceneName();
        stageManager.setDashboardScene(sceneBuilder.buildDashboardScene());
        stageManager.setClientScene(sceneBuilder.buildClientScene());
        stageManager.setDietScene(sceneBuilder.buildDietScene());
        stageManager.setDietGeneratorScene(sceneBuilder.buildDietGeneratorScene());
        stageManager.setTrendScene(sceneBuilder.buildTrendScene());
        stageManager.switchScene(current);
    }
}
