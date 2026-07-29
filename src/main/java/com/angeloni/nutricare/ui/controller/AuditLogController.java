package com.angeloni.nutricare.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.entity.AuditLogEntity;
import com.angeloni.nutricare.service.AuditLogService;

import javafx.application.Platform;
import javafx.scene.control.TableView;

@Component
public class AuditLogController {

    @Autowired private AuditLogService auditLogService;

    private TableView<AuditLogEntity> table;

    public void setup(TableView<AuditLogEntity> table) {
        this.table = table;
        refresh();
    }

    public void refresh() {
        if (table == null) return;
        List<AuditLogEntity> items = auditLogService.findRecent();
        if (Platform.isFxApplicationThread()) {
            table.getItems().setAll(items);
        } else {
            Platform.runLater(() -> table.getItems().setAll(items));
        }
    }
}