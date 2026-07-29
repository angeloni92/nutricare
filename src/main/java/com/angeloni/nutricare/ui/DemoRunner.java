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
import com.angeloni.nutricare.service.I18nService;
import com.angeloni.nutricare.service.UserContextService;
import com.angeloni.nutricare.ui.controller.DietGeneratorController;
import com.angeloni.nutricare.ui.controller.TrendController;
import com.angeloni.nutricare.ui.dialog.AnthropometryFormDialog;
import com.angeloni.nutricare.ui.dialog.ClientFormDialog;
import com.angeloni.nutricare.ui.dialog.DietResultDialog;
import com.angeloni.nutricare.ui.dialog.LoginDialog;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
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
    @Autowired private I18nService i18nService;
    @Autowired private AnthropometryFormDialog anthropometryFormDialog;
    @Autowired private DietResultRepository dietResultRepository;
    @Autowired private UserContextService userContextService;
    @Autowired private DietGeneratorController dietGeneratorController;
    @Autowired private TrendController trendController;

    private Path outputDir;
    private Path framesDir;

    private static final int[] DURATIONS = {2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4};
    private static final String[] FRAME_NAMES = {
        "00_login", "01_dashboard", "02_clienti", "03_nuovo_cliente",
        "04_antro_base", "05_antro_pliche", "06_antro_circ",
        "07_genera_dieta", "08_piano_pdf", "09_piano_word", "10_storico_diete",
        "11_trend"
    };

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
                Thread.sleep(3500);
                runSequence();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Sequenza demo
    // ─────────────────────────────────────────────────────────────────────

    private void runSequence() throws InterruptedException {
        log.info("Demo sequence starting...");

        // 0. Schermata di login
        captureLoginDialog();

        // 1. Dashboard
        navigateAndCapture("dashboard", null, "01_dashboard", 2000);

        // 2. Lista clienti
        navigateAndCapture("client", null, "02_clienti", 2000);

        // 3. Form "Nuovo Cliente" pre-compilato con Francesca Romano
        captureFormDialog(
            () -> ClientFormDialog.showForDemo("Francesca", "Romano", 34, "Italia"),
            "03_nuovo_cliente", 2500);

        // 4-6. Form "Nuova Visita" — Dati Base, Pliche Cutanee, Circonferenze
        captureAntropometriaDialog();

        // 7. Genera Dieta con Claude selezionato
        navigateAndCapture("diet-generator", dietGeneratorController::selectForDemo,
                "07_genera_dieta", 2500);

        // 8. Piano nutrizionale — modalità PDF
        openDietDialogAndCapture("08_piano_pdf");

        // 9. Stessa dialog — seleziona Word (header diventa blu)
        switchToWordAndCapture("09_piano_word");

        // 10. Storico diete
        navigateAndCapture("diet", null, "10_storico_diete", 3000);

        // 11. Trend — andamento peso e BMI del primo cliente
        navigateAndCapture("trend", trendController::selectForDemo, "11_trend", 3500);

        log.info("All {} frames captured. Assembling video...", FRAME_NAMES.length);
        assembleVideo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers navigazione e cattura
    // ─────────────────────────────────────────────────────────────────────

    private void captureLoginDialog() throws InterruptedException {
        runOnFx(() -> LoginDialog.buildForCapture().show());
        Thread.sleep(1500);
        captureAndCloseTopDialog("00_login");
    }

    private void navigateAndCapture(String scene, Runnable extraSetup, String frameName, long holdMs)
            throws InterruptedException {
        runOnFx(() -> {
            stageManager.switchScene(scene);
            if (extraSetup != null) extraSetup.run();
        });
        Thread.sleep(holdMs);
        captureMainScene(frameName);
    }

    private void captureFormDialog(Runnable stageOpener, String frameName, long holdMs)
            throws InterruptedException {
        runOnFx(stageOpener);
        Thread.sleep(holdMs);
        captureAndCloseTopDialog(frameName);
    }

    private void captureAntropometriaDialog() throws InterruptedException {
        // Apre la dialog con tutte e 3 le tab pre-compilate
        runOnFx(() -> anthropometryFormDialog.showForDemo("Marco Rossi", 165.0, 62.0));
        Thread.sleep(1800);

        // Frame 4: tab Dati Base (già selezionata)
        captureTopDialog("04_antro_base", false);

        // Frame 5: switcha a Pliche Cutanee
        switchTabInTopDialog(1);
        Thread.sleep(600);
        captureTopDialog("05_antro_pliche", false);

        // Frame 6: switcha a Circonferenze, poi chiude
        switchTabInTopDialog(2);
        Thread.sleep(600);
        captureTopDialog("06_antro_circ", true);
    }

    private void switchTabInTopDialog(int tabIndex) throws InterruptedException {
        runOnFx(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage s && s != stageManager.getPrimaryStage() && s.isShowing()) {
                    findTabPaneInNode(s.getScene().getRoot(), tabIndex);
                    break;
                }
            }
        });
    }

    private void findTabPaneInNode(Node node, int tabIndex) {
        if (node instanceof javafx.scene.control.TabPane tp) {
            tp.getSelectionModel().select(tabIndex);
            return;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                findTabPaneInNode(child, tabIndex);
            }
        }
    }

    private void openDietDialogAndCapture(String frameName) throws InterruptedException {
        UserEntity user = userContextService.getCurrentUser();
        List<DietResultEntity> diets = dietResultRepository.findByUser(user);
        if (diets.isEmpty()) { log.warn("No diets found, skipping diet dialog frame"); return; }
        DietResultEntity diet = diets.get(0);

        runOnFx(() -> {
            stageManager.switchScene("diet");
            DietResultDialog.show(diet.getGeneratedDiet(), "Marco Rossi", "Claude Sonnet 5", i18nService);
        });
        Thread.sleep(1800);

        // Cattura senza chiudere — la dialog resta aperta per il frame Word
        captureTopDialog(frameName, false);
    }

    private void switchToWordAndCapture(String frameName) throws InterruptedException {
        // Seleziona il radio button Word nella dialog aperta
        runOnFx(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage s && s != stageManager.getPrimaryStage() && s.isShowing()) {
                    selectWordRadioInScene(s.getScene());
                    break;
                }
            }
        });
        Thread.sleep(700); // attende il re-render (header cambia colore)

        // Cattura e chiude
        captureTopDialog(frameName, true);
    }

    private void captureMainScene(String frameName) throws InterruptedException {
        runOnFx(() -> {
            Scene scene = stageManager.getPrimaryStage().getScene();
            WritableImage img = scene.snapshot(null);
            saveImage(img, framesDir.resolve(frameName + ".png"));
        });
    }

    private void captureTopDialog(String frameName, boolean closeAfter) throws InterruptedException {
        runOnFx(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage s && s != stageManager.getPrimaryStage() && s.isShowing()) {
                    WritableImage img = s.getScene().snapshot(null);
                    saveImage(img, framesDir.resolve(frameName + ".png"));
                    if (closeAfter) s.close();
                    break;
                }
            }
        });
    }

    private void captureAndCloseTopDialog(String frameName) throws InterruptedException {
        captureTopDialog(frameName, true);
    }

    private void selectWordRadioInScene(Scene scene) {
        selectWordInNode(scene.getRoot());
    }

    private void selectWordInNode(Node node) {
        if (node instanceof RadioButton rb && "Word (.docx)".equals(rb.getText())) {
            rb.setSelected(true);
            return;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                selectWordInNode(child);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Utilità
    // ─────────────────────────────────────────────────────────────────────

    private void runOnFx(Runnable action) throws InterruptedException {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.runLater(() -> {
            try { action.run(); }
            finally { done.complete(null); }
        });
        done.join();
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

    // ─────────────────────────────────────────────────────────────────────
    // Assemblaggio video con ffmpeg + audio generato
    // ─────────────────────────────────────────────────────────────────────

    private void assembleVideo() {
        try {
            String framesAbs = framesDir.toAbsolutePath().toString().replace("\\", "/");
            Files.writeString(outputDir.resolve("frames_list.txt"), buildConcatFile(framesAbs));

            Path ffmpeg = findFfmpeg();
            if (ffmpeg == null) {
                log.warn("ffmpeg not found. Frames are at: {}", framesDir);
                return;
            }

            Path outputMp4 = outputDir.resolve("nutricare-demo.mp4");
            log.info("Running ffmpeg → {}", outputMp4);

            ProcessBuilder pb = new ProcessBuilder(
                ffmpeg.toString(),
                "-f", "concat", "-safe", "0",
                "-i", outputDir.resolve("frames_list.txt").toString(),
                "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2,format=yuv420p",
                "-c:v", "libx264", "-preset", "slow", "-crf", "18",
                "-an",
                "-y", outputMp4.toString()
            );
            pb.redirectErrorStream(true);
            pb.redirectOutput(outputDir.resolve("ffmpeg.log").toFile());
            int exit = pb.start().waitFor();

            if (exit == 0) {
                log.info("=== VIDEO DEMO CREATO: {} ===", outputMp4);
                Platform.runLater(() -> {
                    javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.INFORMATION,
                            "Video demo creato!\n\n" + outputMp4,
                            javafx.scene.control.ButtonType.OK);
                    a.setTitle("Demo completata");
                    a.showAndWait();
                    System.exit(0);
                });
            } else {
                log.error("ffmpeg failed (exit {}). Log: {}", exit, outputDir.resolve("ffmpeg.log"));
            }
        } catch (Exception e) {
            log.error("Video assembly failed: {}", e.getMessage(), e);
        }
    }

    private String buildConcatFile(String framesAbs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < FRAME_NAMES.length; i++) {
            sb.append("file '").append(framesAbs).append("/").append(FRAME_NAMES[i]).append(".png'\n");
            sb.append("duration ").append(DURATIONS[i]).append("\n");
        }
        // ffmpeg concat demuxer richiede il file finale duplicato
        sb.append("file '").append(framesAbs).append("/").append(FRAME_NAMES[FRAME_NAMES.length - 1]).append(".png'\n");
        sb.append("duration 0.001\n");
        return sb.toString();
    }

    private Path findFfmpeg() {
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

        for (String candidate : List.of(
                "C:/ffmpeg/bin/ffmpeg.exe",
                "C:/Program Files/ffmpeg/bin/ffmpeg.exe")) {
            Path p = Path.of(candidate);
            if (Files.exists(p)) return p;
        }

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
