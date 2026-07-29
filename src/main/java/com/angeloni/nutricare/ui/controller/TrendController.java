package com.angeloni.nutricare.ui.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.AnthropometryEntity;
import com.angeloni.nutricare.repository.AnthropometryRepository;
import com.angeloni.nutricare.service.ClientService;
import com.angeloni.nutricare.service.I18nService;

import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

@Controller
public class TrendController {

    private static final DateTimeFormatter DATE_FMT    = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private AnthropometryRepository anthropometryRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private I18nService i18n;

    private ComboBox<ClientDto> clientCombo;
    private LineChart<String, Number> weightChart;
    private LineChart<String, Number> bmiChart;
    private VBox noDataBox;
    private VBox chartsBox;

    private Label weightStatVal;
    private Label bmiStatVal;
    private Label deltaStatVal;
    private Label lastVisitStatVal;

    public void setup(ComboBox<ClientDto> clientCombo,
                      LineChart<String, Number> weightChart,
                      LineChart<String, Number> bmiChart,
                      VBox noDataBox,
                      VBox chartsBox,
                      Label weightStatVal,
                      Label bmiStatVal,
                      Label deltaStatVal,
                      Label lastVisitStatVal) {
        this.clientCombo      = clientCombo;
        this.weightChart      = weightChart;
        this.bmiChart         = bmiChart;
        this.noDataBox        = noDataBox;
        this.chartsBox        = chartsBox;
        this.weightStatVal    = weightStatVal;
        this.bmiStatVal       = bmiStatVal;
        this.deltaStatVal     = deltaStatVal;
        this.lastVisitStatVal = lastVisitStatVal;

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

    public void selectForDemo() {
        if (clientCombo != null && !clientCombo.getItems().isEmpty()) {
            clientCombo.setValue(clientCombo.getItems().get(0));
        }
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
            resetStats();
            return;
        }

        chartsBox.setVisible(true);
        chartsBox.setManaged(true);
        noDataBox.setVisible(false);
        noDataBox.setManaged(false);

        XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
        weightSeries.setName(i18n.t("trend.axis.weight"));
        XYChart.Series<String, Number> bmiSeries = new XYChart.Series<>();
        bmiSeries.setName(i18n.t("trend.axis.bmi"));

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

        updateStats(data);
    }

    private void updateStats(List<AnthropometryEntity> data) {
        AnthropometryEntity last  = data.get(data.size() - 1);
        AnthropometryEntity first = data.get(0);

        if (last.getWeight() != null) {
            weightStatVal.setText(String.format("%.1f kg", last.getWeight()));
        } else {
            weightStatVal.setText("—");
        }

        if (last.getHeight() != null && last.getWeight() != null && last.getHeight() > 0) {
            double bmi = last.getWeight() / Math.pow(last.getHeight() / 100.0, 2);
            bmiStatVal.setText(String.format("%.1f", bmi));
        } else {
            bmiStatVal.setText("—");
        }

        if (first.getWeight() != null && last.getWeight() != null) {
            double delta = last.getWeight() - first.getWeight();
            String sign = delta > 0 ? "+" : "";
            deltaStatVal.setText(String.format("%s%.1f kg", sign, delta));
            String color = delta < 0 ? "#10b981" : (delta > 0 ? "#ef4444" : "#64748b");
            deltaStatVal.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        } else {
            deltaStatVal.setText("—");
        }

        if (last.getCreatedAt() != null) {
            lastVisitStatVal.setText(last.getCreatedAt().format(DISPLAY_FMT));
        } else {
            lastVisitStatVal.setText("—");
        }
    }

    private void resetStats() {
        weightStatVal.setText("—");
        bmiStatVal.setText("—");
        deltaStatVal.setText("—");
        lastVisitStatVal.setText("—");
    }
}
