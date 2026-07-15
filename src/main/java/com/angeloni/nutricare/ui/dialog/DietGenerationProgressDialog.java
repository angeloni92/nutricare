package com.angeloni.nutricare.ui.dialog;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DietGenerationProgressDialog {

    private final Stage stage;
    private final ProgressBar progressBar;
    private final Label percentLabel;
    private final Label statusLabel;
    private final Timeline timeline;
    private double progress = 0.0;
    private volatile boolean done = false;

    public DietGenerationProgressDialog() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        Label titleLbl = new Label("Generazione Dieta in corso...");
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label aiLbl = new Label("L'intelligenza artificiale sta elaborando il piano nutrizionale");
        aiLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        aiLbl.setWrapText(true);

        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(360);
        progressBar.setPrefHeight(14);
        progressBar.setStyle(
            "-fx-accent: #6366f1;" +
            "-fx-background-color: #e2e8f0;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 0;"
        );

        percentLabel = new Label("0%");
        percentLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");

        statusLabel = new Label("Connessione al provider AI...");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-font-style: italic;");

        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(32, 40, 32, 40));
        root.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 4);"
        );
        root.getChildren().addAll(titleLbl, aiLbl, percentLabel, progressBar, statusLabel);

        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);

        timeline = buildTimeline();
    }

    private Timeline buildTimeline() {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(250), e -> {
            if (done) return;
            if (progress < 0.70) {
                progress += 0.018;
            } else if (progress < 0.85) {
                progress += 0.006;
            } else if (progress < 0.92) {
                progress += 0.002;
            }
            progress = Math.min(progress, 0.92);
            updateUI(progress);
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    private void updateUI(double p) {
        progressBar.setProgress(p);
        int pct = (int) Math.round(p * 100);
        percentLabel.setText(pct + "%");
        if (pct < 20)      statusLabel.setText("Connessione al provider AI...");
        else if (pct < 40) statusLabel.setText("Analisi del profilo cliente...");
        else if (pct < 60) statusLabel.setText("Elaborazione piano nutrizionale...");
        else if (pct < 75) statusLabel.setText("Ottimizzazione calorie e macronutrienti...");
        else if (pct < 88) statusLabel.setText("Definizione dei pasti settimanali...");
        else               statusLabel.setText("Finalizzazione in corso...");
    }

    public void show() {
        stage.show();
        timeline.play();
    }

    public void done() {
        done = true;
        timeline.stop();
        Platform.runLater(() -> {
            updateUI(1.0);
            PauseTransition pause = new PauseTransition(Duration.millis(600));
            pause.setOnFinished(e -> stage.close());
            pause.play();
        });
    }
}
