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

import com.aegisvault.enterprise.DeploymentReport;
import com.aegisvault.enterprise.EnterpriseConfig;
import com.aegisvault.enterprise.MultiVaultManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class EnterpriseSettingsDialog extends Dialog<Void> {

    private final EnterpriseConfig config;
    private final MultiVaultManager vaultManager;
    private final Stage ownerStage;

    public EnterpriseSettingsDialog(EnterpriseConfig config, MultiVaultManager vaultManager, Stage ownerStage) {
        this.config = config;
        this.vaultManager = vaultManager;
        this.ownerStage = ownerStage;

        setTitle("Enterprise Settings");
        setHeaderText(config.isManaged() ? "Managed Configuration — " + config.getConfigSource()
                : "Enterprise Configuration (Not Managed)");
        initOwner(ownerStage);
        setResizable(true);

        getDialogPane().setContent(buildContent());
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().setPrefSize(700, 550);
    }

    @SuppressWarnings("unchecked")
    private TabPane buildContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab policyTab = new Tab("Policy Settings", buildPolicyView());
        Tab vaultsTab = new Tab("Vault Inventory", buildVaultInventoryView());
        Tab systemTab = new Tab("System Info", buildSystemInfoView());

        tabPane.getTabs().addAll(policyTab, vaultsTab, systemTab);
        return tabPane;
    }

    private VBox buildPolicyView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label managedLabel = new Label(config.isManaged() ? "🔒 This installation is managed by enterprise policy"
                : "🔓 This installation is not managed");
        managedLabel.setStyle(config.isManaged() ? "-fx-font-weight: bold; -fx-text-fill: #1565c0;"
                : "-fx-font-weight: bold; -fx-text-fill: #666;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;
        addPolicyRow(grid, row++, "Minimum Password Length", String.valueOf(config.getMinPasswordLength()), "minPasswordLength");
        addPolicyRow(grid, row++, "Max Auto-Lock (minutes)", String.valueOf(config.getMaxAutoLockMinutes()), "maxAutoLockMinutes");
        addPolicyRow(grid, row++, "Cloud Sync Allowed", String.valueOf(config.isSyncAllowed()), "syncAllowed");
        addPolicyRow(grid, row++, "Experimental Ciphers", String.valueOf(config.isExperimentalCiphersAllowed()), "experimentalCiphersAllowed");
        addPolicyRow(grid, row++, "Audit Required", String.valueOf(config.isAuditRequired()), "auditRequired");
        addPolicyRow(grid, row++, "Key Protection Required", String.valueOf(config.isKeyProtectionRequired()), "keyProtectionRequired");
        addPolicyRow(grid, row++, "Audit Retention (days)", String.valueOf(config.getAuditRetentionDays()), "auditRetentionDays");
        addPolicyRow(grid, row++, "Allowed Ciphers", config.getAllowedCiphers(), "allowedCiphers");

        content.getChildren().addAll(managedLabel, new Separator(), grid);
        return content;
    }

    private void addPolicyRow(GridPane grid, int row, String label, String value, String settingKey) {
        boolean locked = config.isPolicyLocked(settingKey);
        Label nameLabel = new Label(label + ":");
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        Label lockLabel = new Label(locked ? "🔒" : "");
        lockLabel.setStyle("-fx-font-size: 14px;");

        if (locked) {
            Tooltip tooltip = new Tooltip("This setting is locked by enterprise policy");
            Tooltip.install(lockLabel, tooltip);
        }

        grid.add(nameLabel, 0, row);
        grid.add(valueLabel, 1, row);
        grid.add(lockLabel, 2, row);
    }

    @SuppressWarnings("unchecked")
    private VBox buildVaultInventoryView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TableView<MultiVaultManager.VaultStatus> table = new TableView<>();

        TableColumn<MultiVaultManager.VaultStatus, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().path()));
        pathCol.setPrefWidth(300);

        TableColumn<MultiVaultManager.VaultStatus, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().status()));
        statusCol.setPrefWidth(100);

        TableColumn<MultiVaultManager.VaultStatus, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().formattedSize()));
        sizeCol.setPrefWidth(100);

        TableColumn<MultiVaultManager.VaultStatus, String> existsCol = new TableColumn<>("Exists");
        existsCol.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().exists() ? "✓" : "✗"));
        existsCol.setPrefWidth(60);

        table.getColumns().addAll(pathCol, statusCol, sizeCol, existsCol);

        List<MultiVaultManager.VaultStatus> statuses = vaultManager.getAllVaultStatuses();
        table.setItems(FXCollections.observableArrayList(statuses));
        table.setPlaceholder(new Label("No vaults registered"));

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> table.setItems(
                FXCollections.observableArrayList(vaultManager.getAllVaultStatuses())));

        Button exportBtn = new Button("Export Deployment Report...");
        exportBtn.setOnAction(e -> handleExportReport());

        HBox buttonBar = new HBox(10, refreshBtn, new Region(), exportBtn);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonBar.getChildren().get(1), Priority.ALWAYS);

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(table, buttonBar);
        return content;
    }

    private VBox buildSystemInfoView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        DeploymentReport report = new DeploymentReport(config, vaultManager);
        Map<String, String> sysInfo = report.getSystemInfo();

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;
        for (Map.Entry<String, String> entry : sysInfo.entrySet()) {
            Label keyLabel = new Label(entry.getKey() + ":");
            keyLabel.setStyle("-fx-font-weight: bold;");
            Label valueLabel = new Label(entry.getValue());
            valueLabel.setWrapText(true);

            grid.add(keyLabel, 0, row);
            grid.add(valueLabel, 1, row);
            row++;
        }

        content.getChildren().addAll(grid);
        return content;
    }

    private void handleExportReport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Deployment Report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        chooser.setInitialFileName("aegisvault_deployment_report.json");
        File file = chooser.showSaveDialog(ownerStage);

        if (file != null) {
            try {
                DeploymentReport report = new DeploymentReport(config, vaultManager);
                String json = report.generateJson();
                Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report Exported");
                alert.setHeaderText(null);
                alert.setContentText("Deployment report saved to:\n" + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to export report: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
}
