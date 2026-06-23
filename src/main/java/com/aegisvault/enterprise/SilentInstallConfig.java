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

import java.util.Properties;

public class SilentInstallConfig {

    private boolean silentMode;
    private boolean acceptLicense;
    private String installPath;
    private String defaultVaultPath;
    private boolean createDesktopShortcut;
    private boolean createStartMenuEntry;
    private String enterpriseConfigPath;

    public SilentInstallConfig() {
        this.silentMode = false;
        this.acceptLicense = false;
        this.installPath = "";
        this.defaultVaultPath = "";
        this.createDesktopShortcut = true;
        this.createStartMenuEntry = true;
        this.enterpriseConfigPath = "";
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public boolean isAcceptLicense() {
        return acceptLicense;
    }

    public void setAcceptLicense(boolean acceptLicense) {
        this.acceptLicense = acceptLicense;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getDefaultVaultPath() {
        return defaultVaultPath;
    }

    public void setDefaultVaultPath(String defaultVaultPath) {
        this.defaultVaultPath = defaultVaultPath;
    }

    public boolean isCreateDesktopShortcut() {
        return createDesktopShortcut;
    }

    public void setCreateDesktopShortcut(boolean createDesktopShortcut) {
        this.createDesktopShortcut = createDesktopShortcut;
    }

    public boolean isCreateStartMenuEntry() {
        return createStartMenuEntry;
    }

    public void setCreateStartMenuEntry(boolean createStartMenuEntry) {
        this.createStartMenuEntry = createStartMenuEntry;
    }

    public String getEnterpriseConfigPath() {
        return enterpriseConfigPath;
    }

    public void setEnterpriseConfigPath(String enterpriseConfigPath) {
        this.enterpriseConfigPath = enterpriseConfigPath;
    }

    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("silent.mode", String.valueOf(silentMode));
        props.setProperty("accept.license", String.valueOf(acceptLicense));
        props.setProperty("install.path", installPath);
        props.setProperty("default.vault.path", defaultVaultPath);
        props.setProperty("create.desktop.shortcut", String.valueOf(createDesktopShortcut));
        props.setProperty("create.startmenu.entry", String.valueOf(createStartMenuEntry));
        props.setProperty("enterprise.config.path", enterpriseConfigPath);
        return props;
    }

    public static SilentInstallConfig fromProperties(Properties props) {
        SilentInstallConfig config = new SilentInstallConfig();
        config.setSilentMode(Boolean.parseBoolean(props.getProperty("silent.mode", "false")));
        config.setAcceptLicense(Boolean.parseBoolean(props.getProperty("accept.license", "false")));
        config.setInstallPath(props.getProperty("install.path", ""));
        config.setDefaultVaultPath(props.getProperty("default.vault.path", ""));
        config.setCreateDesktopShortcut(Boolean.parseBoolean(props.getProperty("create.desktop.shortcut", "true")));
        config.setCreateStartMenuEntry(Boolean.parseBoolean(props.getProperty("create.startmenu.entry", "true")));
        config.setEnterpriseConfigPath(props.getProperty("enterprise.config.path", ""));
        return config;
    }
}
