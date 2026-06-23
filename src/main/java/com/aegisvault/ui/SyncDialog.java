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

import com.aegisvault.sync.SyncConfig;
import com.aegisvault.sync.SyncEngine;
import com.aegisvault.sync.SyncStatus;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SyncDialog extends Dialog<SyncConfig> {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final SyncConfig config;
    private final SyncEngine syncEngine;
    private final Path vaultPath;
    private final Stage ownerStage;

    private CheckBox enabledCheckbox;
    private ComboBox<SyncConfig.ProviderType> providerCombo;
    private TextField remotePathField;
    private TextField webdavUrlField;
    private TextField webdavUserField;
    private PasswordField webdavPasswordField;
    private ComboBox<SyncConfig.SyncFrequency> frequencyCombo;
    private ComboBox<SyncConfig.ConflictResolution> conflictCombo;
    private Label statusLabel;
    private Label lastSyncLabel;
    private ProgressBar syncProgress;
    private VBox localFolderConfig;
    private VBox webdavConfig;

    public SyncDialog(SyncConfig config, SyncEngine syncEngine, Path vaultPath, Stage ownerStage) {
        this.config = config;
        this.syncEngine = syncEngine;
        this.vaultPath = vaultPath;
        this.ownerStage = ownerStage;

        setTitle("Cloud Vault Synchronization");
        setHeaderText("Configure encrypted vault synchronization");
        initOwner(ownerStage);
        setResizable(true);

        getDialogPane().setContent(buildContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setPrefWidth(550);

        populateFields();

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return buildConfig();
            }
            return null;
        });
    }

    private VBox buildContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        enabledCheckbox = new CheckBox("Enable cloud synchronization");
        enabledCheckbox.setStyle("-fx-font-weight: bold;");

        Label warningLabel = new Label(
                "⚠️ Only the encrypted vault file is synced. No plaintext data leaves your machine.");
        warningLabel.setWrapText(true);
        warningLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

        VBox providerSection = buildProviderSection();
        VBox frequencySection = buildFrequencySection();
        VBox statusSection = buildStatusSection();

        enabledCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            providerSection.setDisable(!newVal);
            frequencySection.setDisable(!newVal);
        });

        content.getChildren().addAll(enabledCheckbox, warningLabel,
                new Separator(), providerSection,
                new Separator(), frequencySection,
                new Separator(), statusSection);

        return content;
    }

    private VBox buildProviderSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(5, 0, 5, 0));

        Label title = new Label("Sync Provider");
        title.setStyle("-fx-font-weight: bold;");

        providerCombo = new ComboBox<>();
        providerCombo.getItems().addAll(SyncConfig.ProviderType.values());

        localFolderConfig = buildLocalFolderConfig();
        webdavConfig = buildWebDavConfig();
        webdavConfig.setVisible(false);
        webdavConfig.setManaged(false);

        providerCombo.setOnAction(e -> {
            boolean isLocal = providerCombo.getValue() == SyncConfig.ProviderType.LOCAL_FOLDER;
            localFolderConfig.setVisible(isLocal);
            localFolderConfig.setManaged(isLocal);
            webdavConfig.setVisible(!isLocal);
            webdavConfig.setManaged(!isLocal);
        });

        section.getChildren().addAll(title, providerCombo, localFolderConfig, webdavConfig);
        return section;
    }

    private VBox buildLocalFolderConfig() {
        VBox box = new VBox(5);

        Label label = new Label("Sync Folder (e.g., Dropbox, OneDrive, Google Drive folder):");
        remotePathField = new TextField();
        remotePathField.setPromptText("Select a sync folder...");
        remotePathField.setPrefWidth(400);

        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Sync Folder");
            File dir = chooser.showDialog(ownerStage);
            if (dir != null) {
                remotePathField.setText(dir.getAbsolutePath());
            }
        });

        HBox pathBox = new HBox(10, remotePathField, browseBtn);
        pathBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(remotePathField, Priority.ALWAYS);

        box.getChildren().addAll(label, pathBox);
        return box;
    }

    private VBox buildWebDavConfig() {
        VBox box = new VBox(8);

        webdavUrlField = new TextField();
        webdavUrlField.setPromptText("https://cloud.example.com/remote.php/dav/files/user/");

        webdavUserField = new TextField();
        webdavUserField.setPromptText("Username");

        webdavPasswordField = new PasswordField();
        webdavPasswordField.setPromptText("Password");

        Button testBtn = new Button("Test Connection");
        testBtn.setOnAction(e -> testWebDavConnection());

        box.getChildren().addAll(
                new Label("WebDAV URL:"), webdavUrlField,
                new Label("Username:"), webdavUserField,
                new Label("Password:"), webdavPasswordField,
                testBtn);
        return box;
    }

    private VBox buildFrequencySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(5, 0, 5, 0));

        Label title = new Label("Sync Settings");
        title.setStyle("-fx-font-weight: bold;");

        HBox freqBox = new HBox(10);
        freqBox.setAlignment(Pos.CENTER_LEFT);
        frequencyCombo = new ComboBox<>();
        frequencyCombo.getItems().addAll(SyncConfig.SyncFrequency.values());
        freqBox.getChildren().addAll(new Label("Frequency:"), frequencyCombo);

        HBox conflictBox = new HBox(10);
        conflictBox.setAlignment(Pos.CENTER_LEFT);
        conflictCombo = new ComboBox<>();
        conflictCombo.getItems().addAll(SyncConfig.ConflictResolution.values());
        conflictBox.getChildren().addAll(new Label("On Conflict:"), conflictCombo);

        section.getChildren().addAll(title, freqBox, conflictBox);
        return section;
    }

    private VBox buildStatusSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(5, 0, 5, 0));

        Label title = new Label("Sync Status");
        title.setStyle("-fx-font-weight: bold;");

        statusLabel = new Label("Not synced");
        lastSyncLabel = new Label("Last sync: Never");
        syncProgress = new ProgressBar(0);
        syncProgress.setPrefWidth(400);
        syncProgress.setVisible(false);

        Button syncNowBtn = new Button("Sync Now");
        syncNowBtn.setOnAction(e -> handleSyncNow());

        HBox statusBox = new HBox(10, statusLabel, new Region(), syncNowBtn);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statusBox.getChildren().get(1), Priority.ALWAYS);

        section.getChildren().addAll(title, statusBox, lastSyncLabel, syncProgress);
        return section;
    }

    private void populateFields() {
        enabledCheckbox.setSelected(config.isEnabled());
        providerCombo.setValue(config.getProviderType());
        frequencyCombo.setValue(config.getFrequency());
        conflictCombo.setValue(config.getConflictResolution());

        if (config.getProviderType() == SyncConfig.ProviderType.LOCAL_FOLDER) {
            remotePathField.setText(config.getRemotePath());
        } else {
            String[] parts = config.getRemotePath().split("\\|", 3);
            if (parts.length >= 1) webdavUrlField.setText(parts[0]);
            if (parts.length >= 2) webdavUserField.setText(parts[1]);
            if (parts.length >= 3) webdavPasswordField.setText(parts[2]);
        }

        if (config.getLastSyncTimestamp() > 0) {
            lastSyncLabel.setText("Last sync: " +
                    TIME_FORMAT.format(Instant.ofEpochMilli(config.getLastSyncTimestamp())));
        }

        SyncStatus currentStatus = syncEngine.getStatus();
        statusLabel.setText(currentStatus.getStatusText());

        boolean isLocal = config.getProviderType() == SyncConfig.ProviderType.LOCAL_FOLDER;
        localFolderConfig.setVisible(isLocal);
        localFolderConfig.setManaged(isLocal);
        webdavConfig.setVisible(!isLocal);
        webdavConfig.setManaged(!isLocal);
    }

    private SyncConfig buildConfig() {
        SyncConfig newConfig = new SyncConfig();
        newConfig.setEnabled(enabledCheckbox.isSelected());
        newConfig.setProviderType(providerCombo.getValue());
        newConfig.setFrequency(frequencyCombo.getValue());
        newConfig.setConflictResolution(conflictCombo.getValue());
        newConfig.setLastSyncTimestamp(config.getLastSyncTimestamp());

        if (providerCombo.getValue() == SyncConfig.ProviderType.LOCAL_FOLDER) {
            newConfig.setRemotePath(remotePathField.getText());
        } else {
            newConfig.setRemotePath(webdavUrlField.getText() + "|" +
                    webdavUserField.getText() + "|" +
                    webdavPasswordField.getText());
        }

        return newConfig;
    }

    private void handleSyncNow() {
        if (vaultPath == null) {
            return;
        }

        SyncConfig currentConfig = buildConfig();
        if (currentConfig.getRemotePath().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Configuration Required");
            alert.setContentText("Please configure a sync provider first.");
            alert.showAndWait();
            return;
        }

        syncProgress.setVisible(true);
        syncProgress.setProgress(-1);
        statusLabel.setText("Syncing...");

        syncEngine.syncNow(vaultPath, currentConfig).thenAccept(result -> {
            Platform.runLater(() -> {
                syncProgress.setVisible(false);
                statusLabel.setText(result.success() ? "✓ " + result.message() : "✗ " + result.message());
                if (result.success()) {
                    lastSyncLabel.setText("Last sync: " +
                            TIME_FORMAT.format(Instant.ofEpochMilli(System.currentTimeMillis())));
                }
            });
        });
    }

    private void testWebDavConnection() {
        SyncConfig testConfig = new SyncConfig();
        testConfig.setProviderType(SyncConfig.ProviderType.WEBDAV);
        testConfig.setRemotePath(webdavUrlField.getText() + "|" +
                webdavUserField.getText() + "|" +
                webdavPasswordField.getText());

        syncEngine.testConnection(testConfig).thenAccept(success -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle("Connection Test");
                alert.setHeaderText(null);
                alert.setContentText(success ? "Connection successful!" : "Connection failed.");
                alert.showAndWait();
            });
        });
    }
}
