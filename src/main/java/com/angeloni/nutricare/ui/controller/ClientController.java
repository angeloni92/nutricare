package com.angeloni.nutricare.ui.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.FoldDto;
import com.angeloni.nutricare.service.AnthropometryService;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.ui.StageManager;
import com.angeloni.nutricare.ui.dialog.AnthropometryFormDialog;
import com.angeloni.nutricare.ui.dialog.ClientFormDialog;
import com.angeloni.nutricare.ui.dialog.ExportUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

@Controller
public class ClientController {

    private final ClientService clientService;
    private final AnthropometryService anthropometryService;
    private final StageManager stageManager;

    private TableView<ClientDto> clientTable;
    private TableView<AnthropometryDto> historyTable;
    private Label historyLabel;
    private Button newVisitBtn;
    private Button editBtn;
    private Button deleteBtn;
    private VBox visitDetailBox;
    private Button exportExcelBtn;
    private ObservableList<ClientDto> allClients;
    private ObservableList<AnthropometryDto> visitHistory;

    private static final DateTimeFormatter VISIT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ClientController(ClientService clientService, AnthropometryService anthropometryService,
                            StageManager stageManager) {
        this.clientService = clientService;
        this.anthropometryService = anthropometryService;
        this.stageManager = stageManager;
    }

    public void setup(TableView<ClientDto> clientTable, TextField searchField,
                      Button addBtn, Button editBtn, Button deleteBtn,
                      Button newVisitBtn, TableView<AnthropometryDto> historyTable,
                      Label historyLabel, VBox visitDetailBox, Button exportExcelBtn) {
        this.clientTable    = clientTable;
        this.historyTable   = historyTable;
        this.historyLabel   = historyLabel;
        this.newVisitBtn    = newVisitBtn;
        this.editBtn        = editBtn;
        this.deleteBtn      = deleteBtn;
        this.visitDetailBox = visitDetailBox;
        this.exportExcelBtn = exportExcelBtn;

        this.allClients   = FXCollections.observableArrayList();
        this.visitHistory = FXCollections.observableArrayList();

        FilteredList<ClientDto> filtered = new FilteredList<>(allClients, p -> true);
        clientTable.setItems(filtered);
        historyTable.setItems(visitHistory);

        searchField.textProperty().addListener((obs, o, term) ->
                filtered.setPredicate(c -> {
                    if (term == null || term.isBlank()) return true;
                    String lower = term.toLowerCase();
                    return (c.getName()    != null && c.getName().toLowerCase().contains(lower))
                        || (c.getSurname() != null && c.getSurname().toLowerCase().contains(lower))
                        || (c.getCountry() != null && c.getCountry().toLowerCase().contains(lower));
                }));

        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, o, selected) -> {
            boolean hasSelection = selected != null;
            newVisitBtn.setDisable(!hasSelection);
            editBtn.setDisable(!hasSelection);
            deleteBtn.setDisable(!hasSelection);
            if (hasSelection) {
                historyLabel.setText("Storico visite — " + selected.getName() + " " + selected.getSurname());
                loadHistory(selected.getId());
            } else {
                historyLabel.setText("Seleziona un cliente per vedere lo storico visite");
                visitHistory.clear();
            }
            clearDetailPane();
        });

        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, o, selected) ->
                updateDetailPane(selected));

        newVisitBtn.setDisable(true);
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);

        addBtn.setOnAction(e      -> handleAddClient());
        editBtn.setOnAction(e     -> handleEditClient());
        deleteBtn.setOnAction(e   -> handleDeleteClient());
        newVisitBtn.setOnAction(e -> handleNewVisit());
        exportExcelBtn.setOnAction(e -> handleExportExcel());

        loadClients();
    }

    private void loadClients() {
        try {
            allClients.setAll(clientService.getClients());
            if (clientTable != null) clientTable.refresh();
        } catch (Exception e) {
            showError("Impossibile caricare i clienti: " + e.getMessage());
        }
    }

    private void loadHistory(Long clientId) {
        try {
            visitHistory.setAll(anthropometryService.getVisitsByClient(clientId));
        } catch (Exception e) {
            visitHistory.clear();
        }
    }

    // ─── Detail pane ─────────────────────────────────────────────────────────

    private void clearDetailPane() {
        visitDetailBox.getChildren().clear();
        Label ph = new Label("Seleziona una visita per vedere il dettaglio antropometrico");
        ph.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        visitDetailBox.getChildren().add(ph);
    }

    private void updateDetailPane(AnthropometryDto visit) {
        visitDetailBox.getChildren().clear();
        if (visit == null) { clearDetailPane(); return; }

        String dateStr = visit.getCreatedAt() != null ? visit.getCreatedAt().format(VISIT_FMT) : "-";
        Label titleLbl = new Label("Dettaglio Visita — " + dateStr);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        visitDetailBox.getChildren().addAll(titleLbl, new Separator());

        // Basic data
        Double h = visit.getHeight(), w = visit.getWeight();
        String bmiStr = (h != null && w != null && h > 0)
                ? String.format("%.1f", w / Math.pow(h / 100.0, 2)) : "-";

        VBox basicSection = buildSection("Dati Principali");
        GridPane basicGrid = buildGrid();
        addRow(basicGrid, 0, "Altezza",  h != null ? String.format("%.1f cm", h) : "-");
        addRow(basicGrid, 1, "Peso",     w != null ? String.format("%.1f kg", w) : "-");
        addRow(basicGrid, 2, "BMI",      bmiStr);
        basicSection.getChildren().add(basicGrid);
        visitDetailBox.getChildren().add(basicSection);

        // Folds
        if (visit.getFold() != null) {
            FoldDto f = visit.getFold();
            VBox foldSection = buildSection("Pliche Cutanee (mm)");
            GridPane foldGrid = buildGrid();
            addRow(foldGrid, 0, "Pettorale",      fmt(f.getPectoral()));
            addRow(foldGrid, 1, "Ascellare",      fmt(f.getAxillary()));
            addRow(foldGrid, 2, "Sovrailiaca",    fmt(f.getSuprailiac()));
            addRow(foldGrid, 3, "Addominale",     fmt(f.getAbdominal()));
            addRow(foldGrid, 4, "Tricipite",      fmt(f.getTriceps()));
            addRow(foldGrid, 5, "Sottoscapolare", fmt(f.getSubscapolaris()));
            addRow(foldGrid, 6, "Coscia",         fmt(f.getThigh()));
            foldSection.getChildren().add(foldGrid);
            visitDetailBox.getChildren().add(foldSection);
        }

        // Circumferences
        if (visit.getCircumference() != null) {
            CircumferenceDto c = visit.getCircumference();
            VBox circSection = buildSection("Circonferenze (cm)");
            GridPane circGrid = buildGrid();
            addRow(circGrid, 0, "Petto",   fmt(c.getChest()));
            addRow(circGrid, 1, "Braccio", fmt(c.getArm()));
            addRow(circGrid, 2, "Vita",    fmt(c.getWaist()));
            addRow(circGrid, 3, "Fianchi", fmt(c.getHip()));
            addRow(circGrid, 4, "Coscia",  fmt(c.getThigh()));
            circSection.getChildren().add(circGrid);
            visitDetailBox.getChildren().add(circSection);
        }

        if (visit.getFold() == null && visit.getCircumference() == null) {
            Label none = new Label("Nessun dato di pliche o circonferenze per questa visita.");
            none.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-style: italic;");
            visitDetailBox.getChildren().add(none);
        }
    }

    private VBox buildSection(String title) {
        VBox section = new VBox(8);
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #4f46e5;");
        section.getChildren().add(lbl);
        return section;
    }

    private GridPane buildGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(6);
        grid.setPadding(new Insets(0, 0, 0, 8));
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-min-width: 130;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 12px; -fx-text-fill: #0f172a;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private String fmt(Double d) { return d != null ? String.format("%.1f", d) : "-"; }

    // ─── Excel Export ─────────────────────────────────────────────────────────

    private void handleExportExcel() {
        if (allClients.isEmpty()) { showError("Nessun cliente da esportare."); return; }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Esporta clienti in Excel");
        chooser.setInitialFileName("clienti_nutricare_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));

        File file = chooser.showSaveDialog(exportExcelBtn.getScene().getWindow());
        if (file == null) return;

        try {
            ExportUtils.writeXlsxClients(file, new ArrayList<>(allClients),
                id -> {
                    try { return anthropometryService.getVisitsByClient(id); }
                    catch (Exception e) { return List.of(); }
                });
            showInfo("File Excel esportato:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showError("Errore durante l'esportazione: " + e.getMessage());
        }
    }

    // ─── CRUD handlers ───────────────────────────────────────────────────────

    private void handleAddClient() {
        ClientFormDialog.showCreate().ifPresent(dto -> {
            try {
                clientService.saveClient(dto);
                loadClients();
                stageManager.refreshScene("dashboard");
                showInfo("Cliente aggiunto con successo.");
            } catch (Exception e) {
                showError("Errore durante il salvataggio: " + e.getMessage());
            }
        });
    }

    private void handleEditClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        ClientFormDialog.showEdit(selected).ifPresent(dto -> {
            try {
                clientService.saveClient(dto);
                loadClients();
                showInfo("Cliente aggiornato con successo.");
            } catch (Exception e) {
                showError("Errore durante l'aggiornamento: " + e.getMessage());
            }
        });
    }

    private void handleDeleteClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Eliminare " + selected.getName() + " " + selected.getSurname() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Conferma eliminazione");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    clientService.deleteClientById(selected.getId());
                    loadClients();
                    stageManager.refreshScene("dashboard");
                    visitHistory.clear();
                    clearDetailPane();
                    historyLabel.setText("Seleziona un cliente per vedere lo storico visite");
                    showInfo("Cliente eliminato.");
                } catch (Exception e) {
                    showError("Errore durante l'eliminazione: " + e.getMessage());
                }
            }
        });
    }

    private void handleNewVisit() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        AnthropometryFormDialog.show(selected).ifPresent(dto -> {
            try {
                anthropometryService.saveVisit(selected.getId(), dto);
                loadHistory(selected.getId());
                showInfo("Visita salvata con successo.");
            } catch (Exception e) {
                showError("Errore durante il salvataggio della visita: " + e.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Errore"); a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle("Successo"); a.showAndWait();
    }
}