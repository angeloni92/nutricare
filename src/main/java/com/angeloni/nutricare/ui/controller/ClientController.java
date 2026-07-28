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
import com.angeloni.nutricare.service.I18nService;
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
    private final ClientFormDialog clientFormDialog;
    private final AnthropometryFormDialog anthropometryFormDialog;
    private final I18nService i18n;

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
                            StageManager stageManager, ClientFormDialog clientFormDialog,
                            AnthropometryFormDialog anthropometryFormDialog, I18nService i18n) {
        this.clientService = clientService;
        this.anthropometryService = anthropometryService;
        this.stageManager = stageManager;
        this.clientFormDialog = clientFormDialog;
        this.anthropometryFormDialog = anthropometryFormDialog;
        this.i18n = i18n;
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
                historyLabel.setText(i18n.t("clients.history.current",
                        selected.getName(), selected.getSurname()));
                loadHistory(selected.getId());
            } else {
                historyLabel.setText(i18n.t("clients.history.placeholder"));
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
            showError(i18n.t("clients.alert.load.error", e.getMessage()));
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
        Label ph = new Label(i18n.t("clients.visit.placeholder"));
        ph.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        visitDetailBox.getChildren().add(ph);
    }

    private void updateDetailPane(AnthropometryDto visit) {
        visitDetailBox.getChildren().clear();
        if (visit == null) { clearDetailPane(); return; }

        String dateStr = visit.getCreatedAt() != null ? visit.getCreatedAt().format(VISIT_FMT) : "-";
        Label titleLbl = new Label(i18n.t("clients.detail.title", dateStr));
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        visitDetailBox.getChildren().addAll(titleLbl, new Separator());

        Double h = visit.getHeight(), w = visit.getWeight();
        String bmiStr = (h != null && w != null && h > 0)
                ? String.format("%.1f", w / Math.pow(h / 100.0, 2)) : "-";

        VBox basicSection = buildSection(i18n.t("clients.detail.main"));
        GridPane basicGrid = buildGrid();
        addRow(basicGrid, 0, i18n.t("clients.detail.height"), h != null ? String.format("%.1f cm", h) : "-");
        addRow(basicGrid, 1, i18n.t("clients.detail.weight"), w != null ? String.format("%.1f kg", w) : "-");
        addRow(basicGrid, 2, i18n.t("clients.detail.bmi"),    bmiStr);
        basicSection.getChildren().add(basicGrid);
        visitDetailBox.getChildren().add(basicSection);

        if (visit.getFold() != null) {
            FoldDto f = visit.getFold();
            VBox foldSection = buildSection(i18n.t("clients.detail.folds"));
            GridPane foldGrid = buildGrid();
            addRow(foldGrid, 0, i18n.t("clients.detail.fold.pectoral"),    fmt(f.getPectoral()));
            addRow(foldGrid, 1, i18n.t("clients.detail.fold.axillary"),    fmt(f.getAxillary()));
            addRow(foldGrid, 2, i18n.t("clients.detail.fold.suprailiac"),  fmt(f.getSuprailiac()));
            addRow(foldGrid, 3, i18n.t("clients.detail.fold.abdominal"),   fmt(f.getAbdominal()));
            addRow(foldGrid, 4, i18n.t("clients.detail.fold.triceps"),     fmt(f.getTriceps()));
            addRow(foldGrid, 5, i18n.t("clients.detail.fold.subscapular"), fmt(f.getSubscapolaris()));
            addRow(foldGrid, 6, i18n.t("clients.detail.fold.thigh"),       fmt(f.getThigh()));
            foldSection.getChildren().add(foldGrid);
            visitDetailBox.getChildren().add(foldSection);
        }

        if (visit.getCircumference() != null) {
            CircumferenceDto c = visit.getCircumference();
            VBox circSection = buildSection(i18n.t("clients.detail.circumferences"));
            GridPane circGrid = buildGrid();
            addRow(circGrid, 0, i18n.t("clients.detail.circ.chest"), fmt(c.getChest()));
            addRow(circGrid, 1, i18n.t("clients.detail.circ.arm"),   fmt(c.getArm()));
            addRow(circGrid, 2, i18n.t("clients.detail.circ.waist"), fmt(c.getWaist()));
            addRow(circGrid, 3, i18n.t("clients.detail.circ.hip"),   fmt(c.getHip()));
            addRow(circGrid, 4, i18n.t("clients.detail.circ.thigh"), fmt(c.getThigh()));
            circSection.getChildren().add(circGrid);
            visitDetailBox.getChildren().add(circSection);
        }

        if (visit.getFold() == null && visit.getCircumference() == null) {
            Label none = new Label(i18n.t("clients.detail.no.measures"));
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
        if (allClients.isEmpty()) { showError(i18n.t("clients.export.error.empty")); return; }

        FileChooser chooser = new FileChooser();
        chooser.setTitle(i18n.t("clients.export.dialog.title"));
        chooser.setInitialFileName(i18n.t("clients.export.filename",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));

        File file = chooser.showSaveDialog(exportExcelBtn.getScene().getWindow());
        if (file == null) return;

        try {
            ExportUtils.writeXlsxClients(file, new ArrayList<>(allClients),
                id -> {
                    try { return anthropometryService.getVisitsByClient(id); }
                    catch (Exception e) { return List.of(); }
                }, i18n);
            showInfo(i18n.t("clients.export.success", file.getAbsolutePath()));
        } catch (Exception e) {
            showError(i18n.t("clients.export.error", e.getMessage()));
        }
    }

    // ─── CRUD handlers ───────────────────────────────────────────────────────

    private void handleAddClient() {
        clientFormDialog.showCreate().ifPresent(dto -> {
            try {
                clientService.saveClient(dto);
                loadClients();
                stageManager.refreshScene("dashboard");
                showInfo(i18n.t("clients.alert.add.success"));
            } catch (Exception e) {
                showError(i18n.t("clients.alert.add.error", e.getMessage()));
            }
        });
    }

    private void handleEditClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        clientFormDialog.showEdit(selected).ifPresent(dto -> {
            try {
                clientService.saveClient(dto);
                loadClients();
                showInfo(i18n.t("clients.alert.edit.success"));
            } catch (Exception e) {
                showError(i18n.t("clients.alert.edit.error", e.getMessage()));
            }
        });
    }

    private void handleDeleteClient() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                i18n.t("clients.confirm.delete.msg", selected.getName(), selected.getSurname()),
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle(i18n.t("clients.confirm.delete.title"));
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    clientService.deleteClientById(selected.getId());
                    loadClients();
                    stageManager.refreshScene("dashboard");
                    visitHistory.clear();
                    clearDetailPane();
                    historyLabel.setText(i18n.t("clients.history.placeholder"));
                    showInfo(i18n.t("clients.alert.delete.success"));
                } catch (Exception e) {
                    showError(i18n.t("clients.alert.delete.error", e.getMessage()));
                }
            }
        });
    }

    private void handleNewVisit() {
        ClientDto selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        anthropometryFormDialog.show(selected).ifPresent(dto -> {
            try {
                anthropometryService.saveVisit(selected.getId(), dto);
                loadHistory(selected.getId());
                showInfo(i18n.t("clients.alert.visit.success"));
            } catch (Exception e) {
                showError(i18n.t("clients.alert.visit.error", e.getMessage()));
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(i18n.t("common.error.title"));
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(i18n.t("clients.success.title"));
        a.showAndWait();
    }
}
