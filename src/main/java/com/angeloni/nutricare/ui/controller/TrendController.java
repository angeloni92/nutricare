package com.angeloni.nutricare.ui.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.AnthropometryEntity;
import com.angeloni.nutricare.repository.AnthropometryRepository;
import com.angeloni.nutricare.service.ClientService;

import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

@Controller
public class TrendController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yy");

    @Autowired
    private AnthropometryRepository anthropometryRepository;

    @Autowired
    private ClientService clientService;

    private ComboBox<ClientDto> clientCombo;
    private LineChart<String, Number> weightChart;
    private LineChart<String, Number> bmiChart;
    private VBox noDataBox;
    private VBox chartsBox;

    public void setup(ComboBox<ClientDto> clientCombo,
                      LineChart<String, Number> weightChart,
                      LineChart<String, Number> bmiChart,
                      VBox noDataBox,
                      VBox chartsBox) {
        this.clientCombo = clientCombo;
        this.weightChart = weightChart;
        this.bmiChart    = bmiChart;
        this.noDataBox   = noDataBox;
        this.chartsBox   = chartsBox;

        clientCombo.setConverter(new StringConverter<>() {
            @Override public String toString(ClientDto c) {
                return c == null ? "" : c.getName() + " " + c.getSurname();
            }
            @Override public ClientDto fromString(String s) { return null; }
        });

        clientCombo.setOnAction(e -> {
            ClientDto selected = clientCombo.getValue();
            if (selected != null) loadCharts(selected);
        });

        loadClients();
    }

    public void refresh() {
        ClientDto current = clientCombo != null ? clientCombo.getValue() : null;
        loadClients();
        if (current != null) {
            clientCombo.getItems().stream()
                    .filter(c -> c.getId().equals(current.getId()))
                    .findFirst()
                    .ifPresent(c -> {
                        clientCombo.setValue(c);
                        loadCharts(c);
                    });
        }
    }

    private void loadClients() {
        try {
            List<ClientDto> clients = clientService.getClients();
            clientCombo.setItems(FXCollections.observableArrayList(clients));
        } catch (Exception ignored) {}
    }

    private void loadCharts(ClientDto client) {
        List<AnthropometryEntity> data =
                anthropometryRepository.findByClientIdOrderByCreatedAtAsc(client.getId());

        if (data.isEmpty()) {
            chartsBox.setVisible(false);
            chartsBox.setManaged(false);
            noDataBox.setVisible(true);
            noDataBox.setManaged(true);
            return;
        }

        chartsBox.setVisible(true);
        chartsBox.setManaged(true);
        noDataBox.setVisible(false);
        noDataBox.setManaged(false);

        XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
        weightSeries.setName("Peso (kg)");
        XYChart.Series<String, Number> bmiSeries = new XYChart.Series<>();
        bmiSeries.setName("BMI");

        for (AnthropometryEntity a : data) {
            String date = a.getCreatedAt() != null ? a.getCreatedAt().format(DATE_FMT) : "-";
            if (a.getWeight() != null) {
                weightSeries.getData().add(new XYChart.Data<>(date, a.getWeight()));
            }
            if (a.getHeight() != null && a.getWeight() != null && a.getHeight() > 0) {
                double bmi = a.getWeight() / Math.pow(a.getHeight() / 100.0, 2);
                bmiSeries.getData().add(new XYChart.Data<>(date, Math.round(bmi * 10.0) / 10.0));
            }
        }

        weightChart.getData().setAll(List.of(weightSeries));
        bmiChart.getData().setAll(List.of(bmiSeries));
    }
}
