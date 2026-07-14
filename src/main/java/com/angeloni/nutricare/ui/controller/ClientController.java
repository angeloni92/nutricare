package com.angeloni.nutricare.ui.controller;

import java.util.List;

import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.service.AnthropometryService;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.ui.dialog.AnthropometryFormDialog;
import com.angeloni.nutricare.ui.dialog.ClientFormDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

@Controller
public class ClientController {

    private final ClientService clientService;
    private final AnthropometryService anthropometryService;

    private TableView<ClientDto> clientTable;
    private TableView<AnthropometryDto> historyTable;
    private Label historyLabel;
    private Button newVisitBtn;
    private Button editBtn;
    private Button deleteBtn;
    private ObservableList<ClientDto> allClients;
    private ObservableList<AnthropometryDto> visitHistory;

    public ClientController(ClientService clientService, AnthropometryService anthropometryService) {
        this.clientService = clientService;
        this.anthropometryService = anthropometryService;
    }

    public void setup(TableView<ClientDto> clientTable, TextField searchField,
                      Button addBtn, Button editBtn, Button deleteBtn,
                      Button newVisitBtn, TableView<AnthropometryDto> historyTable,
                      Label historyLabel) {
        this.clientTable  = clientTable;
        this.historyTable = historyTable;
        this.historyLabel = historyLabel;
        this.newVisitBtn  = newVisitBtn;
        this.editBtn      = editBtn;
        this.deleteBtn    = deleteBtn;

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
        });

        newVisitBtn.setDisable(true);
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);

        addBtn.setOnAction(e      -> handleAddClient());
        editBtn.setOnAction(e     -> handleEditClient());
        deleteBtn.setOnAction(e   -> handleDeleteClient());
        newVisitBtn.setOnAction(e -> handleNewVisit());

        loadClients();
    }

    private void loadClients() {
        try {
            List<ClientDto> clients = clientService.getClients();
            allClients.setAll(clients);
            if (clientTable != null) clientTable.refresh();
        } catch (Exception e) {
            showError("Impossibile caricare i clienti: " + e.getMessage());
        }
    }

    private void loadHistory(Long clientId) {
        try {
            List<AnthropometryDto> visits = anthropometryService.getVisitsByClient(clientId);
            visitHistory.setAll(visits);
        } catch (Exception e) {
            visitHistory.clear();
        }
    }

    private void handleAddClient() {
        ClientFormDialog.showCreate().ifPresent(dto -> {
            try {
                clientService.saveClient(dto);
                loadClients();
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
                    visitHistory.clear();
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
