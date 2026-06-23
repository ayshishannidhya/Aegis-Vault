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
package com.aegisvault.enterprise;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.prefs.Preferences;

public final class EnterpriseConfigLoader {

    private static final String CONFIG_FILE_NAME = "aegisvault.properties";
    private static final String REGISTRY_PATH = "SOFTWARE\\AegisVault";

    private EnterpriseConfigLoader() {
    }

    public static EnterpriseConfig load() {
        EnterpriseConfig config = new EnterpriseConfig();

        loadFromDefaults(config);
        loadFromUserConfig(config);
        loadFromSystemConfig(config);
        loadFromRegistry(config);

        return config;
    }

    private static void loadFromDefaults(EnterpriseConfig config) {
        config.setManaged(false);
        config.setConfigSource("defaults");
    }

    private static void loadFromUserConfig(EnterpriseConfig config) {
        String userHome = System.getProperty("user.home");
        Path userConfig = Paths.get(userHome, ".aegisvault", CONFIG_FILE_NAME);

        if (Files.exists(userConfig)) {
            Properties props = loadProperties(userConfig);
            if (props != null) {
                applyProperties(config, props, false);
                config.setConfigSource("user:" + userConfig);
            }
        }
    }

    private static void loadFromSystemConfig(EnterpriseConfig config) {
        String osName = System.getProperty("os.name", "").toLowerCase();
        Path systemConfig;

        if (osName.contains("win")) {
            String programData = System.getenv("ProgramData");
            if (programData == null) {
                programData = "C:\\ProgramData";
            }
            systemConfig = Paths.get(programData, "AegisVault", CONFIG_FILE_NAME);
        } else {
            systemConfig = Paths.get("/etc", "aegisvault", CONFIG_FILE_NAME);
        }

        if (Files.exists(systemConfig)) {
            Properties props = loadProperties(systemConfig);
            if (props != null) {
                applyProperties(config, props, true);
                config.setManaged(true);
                config.setConfigSource("system:" + systemConfig);
            }
        }
    }

    private static void loadFromRegistry(EnterpriseConfig config) {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }

        try {
            Preferences machinePrefs;
            try {
                machinePrefs = Preferences.systemRoot().node(REGISTRY_PATH);
                machinePrefs.keys();
            } catch (Exception e) {
                return;
            }

            String minPwdLength = machinePrefs.get("MinPasswordLength", null);
            if (minPwdLength != null) {
                config.setMinPasswordLength(Integer.parseInt(minPwdLength));
                config.lockSetting("minPasswordLength");
                config.setManaged(true);
            }

            String maxAutoLock = machinePrefs.get("MaxAutoLockMinutes", null);
            if (maxAutoLock != null) {
                config.setMaxAutoLockMinutes(Integer.parseInt(maxAutoLock));
                config.lockSetting("maxAutoLockMinutes");
                config.setManaged(true);
            }

            String syncAllowed = machinePrefs.get("SyncAllowed", null);
            if (syncAllowed != null) {
                config.setSyncAllowed(Boolean.parseBoolean(syncAllowed));
                config.lockSetting("syncAllowed");
                config.setManaged(true);
            }

            String expCiphers = machinePrefs.get("ExperimentalCiphersAllowed", null);
            if (expCiphers != null) {
                config.setExperimentalCiphersAllowed(Boolean.parseBoolean(expCiphers));
                config.lockSetting("experimentalCiphersAllowed");
                config.setManaged(true);
            }

            String auditRequired = machinePrefs.get("AuditRequired", null);
            if (auditRequired != null) {
                config.setAuditRequired(Boolean.parseBoolean(auditRequired));
                config.lockSetting("auditRequired");
                config.setManaged(true);
            }

            String keyProtection = machinePrefs.get("KeyProtectionRequired", null);
            if (keyProtection != null) {
                config.setKeyProtectionRequired(Boolean.parseBoolean(keyProtection));
                config.lockSetting("keyProtectionRequired");
                config.setManaged(true);
            }

            if (config.isManaged()) {
                config.setConfigSource("registry:" + REGISTRY_PATH);
            }
        } catch (Exception ignored) {
        }
    }

    private static Properties loadProperties(Path path) {
        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(path)) {
            props.load(is);
            return props;
        } catch (IOException e) {
            return null;
        }
    }

    private static void applyProperties(EnterpriseConfig config, Properties props, boolean lockSettings) {
        String value;

        value = props.getProperty("min.password.length");
        if (value != null) {
            config.setMinPasswordLength(Integer.parseInt(value.trim()));
            if (lockSettings) config.lockSetting("minPasswordLength");
        }

        value = props.getProperty("max.autolock.minutes");
        if (value != null) {
            config.setMaxAutoLockMinutes(Integer.parseInt(value.trim()));
            if (lockSettings) config.lockSetting("maxAutoLockMinutes");
        }

        value = props.getProperty("sync.allowed");
        if (value != null) {
            config.setSyncAllowed(Boolean.parseBoolean(value.trim()));
            if (lockSettings) config.lockSetting("syncAllowed");
        }

        value = props.getProperty("experimental.ciphers.allowed");
        if (value != null) {
            config.setExperimentalCiphersAllowed(Boolean.parseBoolean(value.trim()));
            if (lockSettings) config.lockSetting("experimentalCiphersAllowed");
        }

        value = props.getProperty("audit.retention.days");
        if (value != null) {
            config.setAuditRetentionDays(Integer.parseInt(value.trim()));
            if (lockSettings) config.lockSetting("auditRetentionDays");
        }

        value = props.getProperty("audit.required");
        if (value != null) {
            config.setAuditRequired(Boolean.parseBoolean(value.trim()));
            if (lockSettings) config.lockSetting("auditRequired");
        }

        value = props.getProperty("key.protection.required");
        if (value != null) {
            config.setKeyProtectionRequired(Boolean.parseBoolean(value.trim()));
            if (lockSettings) config.lockSetting("keyProtectionRequired");
        }

        value = props.getProperty("allowed.ciphers");
        if (value != null) {
            config.setAllowedCiphers(value.trim());
            if (lockSettings) config.lockSetting("allowedCiphers");
        }

        value = props.getProperty("default.vault.path");
        if (value != null) {
            config.setDefaultVaultPath(value.trim());
        }

        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("custom.")) {
                config.setCustomProperty(key.substring(7), props.getProperty(key));
            }
        }
    }
}
