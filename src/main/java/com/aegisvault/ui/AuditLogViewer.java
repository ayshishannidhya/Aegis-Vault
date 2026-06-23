/*
 * Copyright (c) 2026 Aegis Vault
 * All rights reserved.
 *
 * Author: Ayshi Shannidhya Panda
 * Email:  asp45624@gmail.com
 * Web:    https://ayshishannidhya.online
 * GitHub: https://github.com/ayshishannidhya
 *
 * This software, known as "AegisVault-J", including its source code, documentation,
 * design, and associated materials, is the intellectual property of the author.
 *
 * No part of this software may be copied, modified, distributed, or used in
 * derivative works without explicit written permission from the copyright holder,
 * except for academic evaluation purposes.
 *
 * This software is provided "as is", without warranty of any kind, express or
 * implied, including but not limited to the warranties of merchantability,
 * fitness for a particular purpose, and noninfringement.
 */
package com.aegisvault.ui;

import com.aegisvault.audit.AuditEvent;
import com.aegisvault.audit.AuditEventType;
import com.aegisvault.audit.AuditLog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AuditLogViewer extends Dialog<Void> {

    private final AuditLog auditLog;
    private final Stage ownerStage;
    private final ObservableList<AuditEvent> allEvents;
    private final FilteredList<AuditEvent> filteredEvents;
    private final TableView<AuditEvent> tableView;

    public AuditLogViewer(AuditLog auditLog, Stage ownerStage) {
        this.auditLog = auditLog;
        this.ownerStage = ownerStage;
        this.allEvents = FXCollections.observableArrayList();
        this.filteredEvents = new FilteredList<>(allEvents, p -> true);
        this.tableView = new TableView<>();

        setTitle("Audit Log Viewer");
        setHeaderText("Vault Access Audit Log — " + auditLog.getEventCount() + " events");
        initOwner(ownerStage);
        setResizable(true);

        getDialogPane().setContent(buildContent());
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().setPrefSize(900, 600);

        loadEvents();
    }

    private VBox buildContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        HBox filterBar = buildFilterBar();
        buildTableView();

        HBox bottomBar = buildBottomBar();

        VBox.setVgrow(tableView, Priority.ALWAYS);
        content.getChildren().addAll(filterBar, tableView, bottomBar);
        return content;
    }

    private HBox buildFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(5));

        Label filterLabel = new Label("Filter:");

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().add("All Events");
        for (AuditEventType type : AuditEventType.values()) {
            typeFilter.getItems().add(type.getDisplayName());
        }
        typeFilter.setValue("All Events");

        ComboBox<String> severityFilter = new ComboBox<>();
        severityFilter.getItems().addAll("All Severities", "INFO", "WARNING", "SECURITY");
        severityFilter.setValue("All Severities");

        TextField searchField = new TextField();
        searchField.setPromptText("Search details...");
        searchField.setPrefWidth(200);

        typeFilter.setOnAction(e -> applyFilters(typeFilter, severityFilter, searchField));
        severityFilter.setOnAction(e -> applyFilters(typeFilter, severityFilter, searchField));
        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                applyFilters(typeFilter, severityFilter, searchField));

        Label countLabel = new Label();
        filteredEvents.addListener((javafx.collections.ListChangeListener<AuditEvent>) c ->
                countLabel.setText(filteredEvents.size() + " events"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBar.getChildren().addAll(filterLabel, typeFilter, severityFilter, searchField, spacer, countLabel);
        return filterBar;
    }

    private void applyFilters(ComboBox<String> typeFilter, ComboBox<String> severityFilter, TextField searchField) {
        String selectedType = typeFilter.getValue();
        String selectedSeverity = severityFilter.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        filteredEvents.setPredicate(event -> {
            if (!"All Events".equals(selectedType)) {
                if (!event.getEventType().getDisplayName().equals(selectedType)) {
                    return false;
                }
            }

            if (!"All Severities".equals(selectedSeverity)) {
                if (!event.getEventType().getSeverity().name().equals(selectedSeverity)) {
                    return false;
                }
            }

            if (!searchText.isEmpty()) {
                return event.getDetails().toLowerCase().contains(searchText)
                        || event.getSourceUser().toLowerCase().contains(searchText)
                        || event.getSourceHost().toLowerCase().contains(searchText);
            }

            return true;
        });
    }

    @SuppressWarnings("unchecked")
    private void buildTableView() {
        TableColumn<AuditEvent, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFormattedTimestamp()));
        timestampCol.setPrefWidth(160);

        TableColumn<AuditEvent, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEventType().getSeverity().name()));
        severityCol.setPrefWidth(80);
        severityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "INFO" -> setStyle("-fx-text-fill: #2e7d32;");
                        case "WARNING" -> setStyle("-fx-text-fill: #f57f17;");
                        case "SECURITY" -> setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        TableColumn<AuditEvent, String> typeCol = new TableColumn<>("Event");
        typeCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEventType().getDisplayName()));
        typeCol.setPrefWidth(160);

        TableColumn<AuditEvent, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getDetails()));
        detailsCol.setPrefWidth(250);

        TableColumn<AuditEvent, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getSourceUser()));
        userCol.setPrefWidth(100);

        TableColumn<AuditEvent, String> hostCol = new TableColumn<>("Host");
        hostCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getSourceHost()));
        hostCol.setPrefWidth(120);

        tableView.getColumns().addAll(timestampCol, severityCol, typeCol, detailsCol, userCol, hostCol);
        tableView.setItems(filteredEvents);
        tableView.setPlaceholder(new Label("No audit events recorded"));
    }

    private HBox buildBottomBar() {
        HBox bottomBar = new HBox(10);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(5));

        Label summaryLabel = new Label(buildSummary());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportButton = new Button("Export to CSV...");
        exportButton.setOnAction(e -> handleExportCsv());

        bottomBar.getChildren().addAll(summaryLabel, spacer, exportButton);
        return bottomBar;
    }

    private String buildSummary() {
        long securityCount = allEvents.stream()
                .filter(e -> e.getEventType().getSeverity() == AuditEventType.Severity.SECURITY)
                .count();
        long warningCount = allEvents.stream()
                .filter(e -> e.getEventType().getSeverity() == AuditEventType.Severity.WARNING)
                .count();
        return String.format("Total: %d | Security: %d | Warnings: %d",
                allEvents.size(), securityCount, warningCount);
    }

    private void handleExportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Audit Log");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setInitialFileName("audit_log.csv");
        File file = chooser.showSaveDialog(ownerStage);

        if (file != null) {
            try {
                auditLog.exportToCsv(file.toPath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Complete");
                alert.setHeaderText(null);
                alert.setContentText("Audit log exported to:\n" + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to export audit log: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void loadEvents() {
        List<AuditEvent> events = auditLog.getAllEvents();
        allEvents.setAll(events);

        if (!events.isEmpty()) {
            tableView.scrollTo(events.size() - 1);
        }
    }
}
