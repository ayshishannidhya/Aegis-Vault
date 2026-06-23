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

import com.aegisvault.crypto.KeyProtectionConfig;
import com.aegisvault.enterprise.EnterpriseConfig;
import com.aegisvault.enterprise.EnterpriseConfigLoader;
import com.aegisvault.service.VaultService;
import com.aegisvault.sync.SyncEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static final String APP_TITLE = "AegisVault-J";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    private final VaultService vaultService = new VaultService();
    private SyncEngine syncEngine;

    @Override
    public void start(Stage primaryStage) {
        java.util.logging.Logger.getLogger("java.util.prefs").setLevel(java.util.logging.Level.SEVERE);

        EnterpriseConfig enterpriseConfig = EnterpriseConfigLoader.load();
        vaultService.setEnterpriseConfig(enterpriseConfig);

        KeyProtectionConfig keyProtectionConfig = KeyProtectionConfig.load();
        vaultService.setKeyProtectionConfig(keyProtectionConfig);

        syncEngine = new SyncEngine();
        vaultService.setSyncEngine(syncEngine);

        if (enterpriseConfig.isManaged() && !enterpriseConfig.isAutoLockTimeoutAcceptable(
                (int) (vaultService.getAutoLockTimeout() / 60000))) {
            vaultService.setAutoLockTimeout(enterpriseConfig.getMaxAutoLockMinutes() * 60 * 1000L);
        }

        MainController controller = new MainController(vaultService, primaryStage);
        Scene scene = new Scene(controller.getRoot(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        var stylesheet = getClass().getResource("/styles/main.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        var iconStream = getClass().getResourceAsStream("/icons/logo.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setOnCloseRequest(e -> {
            vaultService.close();
            if (syncEngine != null) {
                syncEngine.shutdown();
            }
        });
        primaryStage.show();
    }

    @Override
    public void stop() {
        vaultService.close();
        if (syncEngine != null) {
            syncEngine.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
