package com.angeloni.nutricare.ui.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.service.DietService;
import com.angeloni.nutricare.service.I18nService;
import com.angeloni.nutricare.service.UserContextService;
import com.angeloni.nutricare.ui.dialog.DietResultDialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

@Controller
public class DietController {

    private final DietResultRepository dietResultRepository;
    private final DietService dietService;
    private final ClientService clientService;
    private final UserContextService userContextService;
    private final I18nService i18n;

    private TableView<DietResultEntity> dietTable;
    private final ObservableList<DietResultEntity> dietData = FXCollections.observableArrayList();
    private FilteredList<DietResultEntity> filteredData;
    private final Map<Long, String> clientNames = new HashMap<>();
    private String searchFilter = "";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DietController(DietResultRepository dietResultRepository,
                          DietService dietService,
                          ClientService clientService,
                          UserContextService userContextService,
                          I18nService i18n) {
        this.dietResultRepository = dietResultRepository;
        this.dietService = dietService;
        this.clientService = clientService;
        this.userContextService = userContextService;
        this.i18n = i18n;
    }

    public void setup(TableView<DietResultEntity> table, Button viewBtn, Button deleteBtn) {
        this.dietTable = table;

        TableColumn<DietResultEntity, String> clientCol = new TableColumn<>(i18n.t("diet.table.client"));
        clientCol.setCellValueFactory(cd -> new SimpleStringProperty(getClientName(cd.getValue().getClientId())));

        TableColumn<DietResultEntity, String> modelCol = new TableColumn<>(i18n.t("diet.table.model"));
        modelCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getAiModel() != null ? cd.getValue().getAiModel() : "-"));

        TableColumn<DietResultEntity, String> dateCol = new TableColumn<>(i18n.t("diet.table.date"));
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCreatedAt() != null ? cd.getValue().getCreatedAt().format(DATE_FMT) : "-"));

        table.getColumns().clear();
        table.getColumns().addAll(clientCol, modelCol, dateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        filteredData = new FilteredList<>(dietData, this::matchesFilter);
        table.setItems(filteredData);

        viewBtn.setOnAction(e -> handleView());
        deleteBtn.setOnAction(e -> handleDelete());

        refresh();
    }

    public void setupSearchField(TextField searchField) {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchFilter = newVal == null ? "" : newVal.trim().toLowerCase();
            if (filteredData != null) {
                filteredData.setPredicate(this::matchesFilter);
            }
        });
    }

    private boolean matchesFilter(DietResultEntity item) {
        if (searchFilter.isBlank()) return true;
        String clientName = getClientName(item.getClientId()).toLowerCase();
        return clientName.contains(searchFilter);
    }

    public void refresh() {
        loadClientNames();
        try {
            List<DietResultEntity> diets = dietResultRepository.findByUser(userContextService.getCurrentUser());
            diets.sort((a, b) -> {
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
            dietData.setAll(diets);
            if (filteredData != null) filteredData.setPredicate(this::matchesFilter);
        } catch (Exception e) {
            dietData.clear();
        }
        if (dietTable != null) dietTable.refresh();
    }

    private void loadClientNames() {
        try {
            clientNames.clear();
            for (ClientDto c : clientService.getClients()) {
                clientNames.put(c.getId(), c.getName() + " " + c.getSurname());
            }
        } catch (Exception ignored) {}
    }

    private String getClientName(Long clientId) {
        if (clientId == null) return "-";
        return clientNames.getOrDefault(clientId, i18n.t("diet.client.fallback", clientId));
    }

    private void handleView() {
        DietResultEntity selected = dietTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, i18n.t("diet.warn.select.view"), ButtonType.OK).showAndWait();
            return;
        }
        DietResultDialog.show(
                selected.getGeneratedDiet(),
                getClientName(selected.getClientId()),
                selected.getAiModel() != null ? selected.getAiModel() : "AI");
    }

    private void handleDelete() {
        DietResultEntity selected = dietTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, i18n.t("diet.warn.select.delete"), ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                i18n.t("diet.confirm.delete.msg", getClientName(selected.getClientId())),
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle(i18n.t("diet.confirm.delete.title"));
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    dietService.deleteDiet(selected.getId());
                    dietData.remove(selected);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR,
                            i18n.t("diet.error.delete", e.getMessage()), ButtonType.OK).showAndWait();
                }
            }
        });
    }
}
