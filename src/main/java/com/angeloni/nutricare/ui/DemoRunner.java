package com.angeloni.nutricare.ui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.service.UserContextService;
import com.angeloni.nutricare.ui.controller.DietGeneratorController;
import com.angeloni.nutricare.ui.dialog.DietResultDialog;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(100)
public class DemoRunner {

    @Value("${nutricare.demo.record:false}")
    private boolean demoRecord;

    @Autowired private StageManager stageManager;
    @Autowired private DietResultRepository dietResultRepository;
    @Autowired private UserContextService userContextService;
    @Autowired private DietGeneratorController dietGeneratorController;

    private Path outputDir;
    private Path framesDir;

    @EventListener(ApplicationReadyEvent.class)
    public void maybeRun() {
        if (!demoRecord) return;

        outputDir = Path.of(System.getProperty("user.home"), "nutricare-demo");
        framesDir = outputDir.resolve("frames");
        try {
            Files.createDirectories(framesDir);
        } catch (IOException e) {
            log.error("Cannot create demo output directory: {}", e.getMessage());
            return;
        }

        log.info("=== DEMO RECORDING MODE — output: {} ===", outputDir);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3500); // attende che la UI sia completamente inizializzata
                runSequence();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void runSequence() throws InterruptedException {
        log.info("Demo sequence starting...");

        // 1 — Dashboard
        navigateAndCapture("dashboard", null, "01_dashboard", 2500);

        // 2 — Clienti
        navigateAndCapture("client", null, "02_clienti", 2500);

        // 3 — Storico Diete
        navigateAndCapture("diet", null, "03_storico_diete", 2500);

        // 4 — Genera Dieta (con Claude selezionato)
        navigateAndCapture("diet-generator", dietGeneratorController::selectForDemo, "04_genera_dieta_claude", 2500);

        // 5 — Dialogo piano nutrizionale di Marco Rossi
        captureDietResultDialog();

        // 6 — Dashboard finale
        navigateAndCapture("dashboard", null, "06_dashboard_finale", 2000);

        log.info("All frames captured. Assembling video...");
        assembleVideo();
    }

    private void navigateAndCapture(String scene, Runnable extraSetup, String frameName, long holdMs)
            throws InterruptedException {
        CompletableFuture<Void> navDone = new CompletableFuture<>();
        Platform.runLater(() -> {
            stageManager.switchScene(scene);
            if (extraSetup != null) extraSetup.run();
            navDone.complete(null);
        });
        navDone.join();
        Thread.sleep(holdMs);
        captureMainScene(frameName);
    }

    private void captureMainScene(String frameName) throws InterruptedException {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Scene scene = stageManager.getPrimaryStage().getScene();
                WritableImage img = scene.snapshot(null);
                saveImage(img, framesDir.resolve(frameName + ".png"));
            } finally {
                done.complete(null);
            }
        });
        done.join();
    }

    private void captureDietResultDialog() throws InterruptedException {
        UserEntity user = userContextService.getCurrentUser();
        List<DietResultEntity> diets = dietResultRepository.findByUser(user);
        if (diets.isEmpty()) {
            log.warn("No diet results found, skipping dialog frame");
            return;
        }
        DietResultEntity diet = diets.get(0);

        CompletableFuture<Void> openDone = new CompletableFuture<>();
        Platform.runLater(() -> {
            stageManager.switchScene("diet");
            DietResultDialog.show(diet.getGeneratedDiet(), "Marco Rossi", "Claude Sonnet 5");
            openDone.complete(null);
        });
        openDone.join();
        Thread.sleep(1800); // attende il rendering del dialogo

        CompletableFuture<Void> snapDone = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                for (Window w : Window.getWindows()) {
                    if (w instanceof Stage s && s != stageManager.getPrimaryStage() && s.isShowing()) {
                        WritableImage img = s.getScene().snapshot(null);
                        saveImage(img, framesDir.resolve("05_piano_nutrizionale.png"));
                        s.close();
                        break;
                    }
                }
            } finally {
                snapDone.complete(null);
            }
        });
        snapDone.join();
    }

    private void saveImage(WritableImage img, Path out) {
        try {
            BufferedImage buf = SwingFXUtils.fromFXImage(img, null);
            ImageIO.write(buf, "png", out.toFile());
            log.info("Frame saved: {}", out.getFileName());
        } catch (IOException e) {
            log.error("Failed to save frame {}: {}", out.getFileName(), e.getMessage());
        }
    }

    private void assembleVideo() {
        try {
            String framesAbs = framesDir.toAbsolutePath().toString().replace("\\", "/");
            String concatContent = buildConcatFile(framesAbs);
            Path concatFile = outputDir.resolve("frames_list.txt");
            Files.writeString(concatFile, concatContent);

            Path ffmpeg = findFfmpeg();
            if (ffmpeg == null) {
                log.warn("ffmpeg not found. Frames are at: {}", framesDir);
                log.warn("Run manually: ffmpeg -f concat -safe 0 -i {} -c:v libx264 -pix_fmt yuv420p nutricare-demo.mp4",
                        concatFile);
                return;
            }

            Path outputMp4 = outputDir.resolve("nutricare-demo.mp4");
            log.info("Running ffmpeg → {}", outputMp4);

            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg.toString(),
                    "-f", "concat", "-safe", "0",
                    "-i", concatFile.toString(),
                    "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2,format=yuv420p",
                    "-c:v", "libx264", "-preset", "slow", "-crf", "18",
                    "-y", outputMp4.toString()
            );
            pb.redirectErrorStream(true);
            pb.redirectOutput(outputDir.resolve("ffmpeg.log").toFile());
            int exit = pb.start().waitFor();

            if (exit == 0) {
                log.info("=== VIDEO DEMO CREATO: {} ===", outputMp4);
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION,
                            "Video demo creato con successo!\n\n" + outputMp4, ButtonType.OK);
                    a.setTitle("Demo completata");
                    a.showAndWait();
                    Platform.exit();
                });
            } else {
                log.error("ffmpeg failed (exit {}). Log: {}", exit, outputDir.resolve("ffmpeg.log"));
            }
        } catch (Exception e) {
            log.error("Video assembly failed: {}", e.getMessage(), e);
        }
    }

    private String buildConcatFile(String framesAbs) {
        return "file '" + framesAbs + "/01_dashboard.png'\n" +
               "duration 3\n" +
               "file '" + framesAbs + "/02_clienti.png'\n" +
               "duration 3\n" +
               "file '" + framesAbs + "/03_storico_diete.png'\n" +
               "duration 3\n" +
               "file '" + framesAbs + "/04_genera_dieta_claude.png'\n" +
               "duration 3\n" +
               "file '" + framesAbs + "/05_piano_nutrizionale.png'\n" +
               "duration 5\n" +
               "file '" + framesAbs + "/06_dashboard_finale.png'\n" +
               "duration 3\n" +
               // ffmpeg concat demuxer richiede il file finale duplicato
               "file '" + framesAbs + "/06_dashboard_finale.png'\n" +
               "duration 0.001\n";
    }

    private Path findFfmpeg() {
        // 1. PowerShell Get-Command (funziona anche con PATH aggiornato dopo winget)
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell", "-NoProfile", "-Command",
                    "(Get-Command ffmpeg -ErrorAction SilentlyContinue).Source");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String line = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            p.waitFor();
            if (line != null && !line.isBlank() && !line.contains("null")) {
                Path path = Path.of(line.trim());
                if (Files.exists(path)) return path;
            }
        } catch (Exception ignored) {}

        // 2. Percorsi comuni di installazione winget
        for (String candidate : List.of(
                "C:/ffmpeg/bin/ffmpeg.exe",
                "C:/Program Files/ffmpeg/bin/ffmpeg.exe",
                System.getProperty("user.home") + "/ffmpeg/bin/ffmpeg.exe")) {
            Path p = Path.of(candidate);
            if (Files.exists(p)) return p;
        }

        // 3. WinGet packages directory
        Path wingetPkg = Path.of(System.getProperty("user.home"),
                "AppData/Local/Microsoft/WinGet/Packages");
        if (Files.exists(wingetPkg)) {
            try {
                return Files.walk(wingetPkg, 5)
                        .filter(p -> p.getFileName().toString().equals("ffmpeg.exe"))
                        .findFirst().orElse(null);
            } catch (IOException ignored) {}
        }

        return null;
    }
}
