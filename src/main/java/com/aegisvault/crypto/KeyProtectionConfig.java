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
package com.aegisvault.crypto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class KeyProtectionConfig {

    public enum KeyProtectionMode {
        SOFTWARE_ONLY,
        OS_KEYSTORE,
        OS_KEYSTORE_WITH_FALLBACK
    }

    private static final String PREFS_KEY_PROTECTION_MODE = "keyProtectionMode";
    private static final String PREFS_FALLBACK_DIR = "keyProtectionFallbackDir";

    private KeyProtectionMode mode;
    private Path fallbackDir;

    public KeyProtectionConfig() {
        this.mode = KeyProtectionMode.SOFTWARE_ONLY;
        this.fallbackDir = getDefaultFallbackDir();
    }

    public KeyProtectionConfig(KeyProtectionMode mode, Path fallbackDir) {
        this.mode = mode;
        this.fallbackDir = fallbackDir != null ? fallbackDir : getDefaultFallbackDir();
    }

    public KeyProtectionMode getMode() {
        return mode;
    }

    public void setMode(KeyProtectionMode mode) {
        this.mode = mode;
    }

    public Path getFallbackDir() {
        return fallbackDir;
    }

    public void setFallbackDir(Path fallbackDir) {
        this.fallbackDir = fallbackDir;
    }

    public boolean isHardwareProtectionEnabled() {
        return mode != KeyProtectionMode.SOFTWARE_ONLY;
    }

    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(KeyProtectionConfig.class);
        prefs.put(PREFS_KEY_PROTECTION_MODE, mode.name());
        prefs.put(PREFS_FALLBACK_DIR, fallbackDir.toString());
    }

    public static KeyProtectionConfig load() {
        Preferences prefs = Preferences.userNodeForPackage(KeyProtectionConfig.class);
        String modeName = prefs.get(PREFS_KEY_PROTECTION_MODE, KeyProtectionMode.SOFTWARE_ONLY.name());
        String fallbackDirStr = prefs.get(PREFS_FALLBACK_DIR, getDefaultFallbackDir().toString());

        KeyProtectionMode mode;
        try {
            mode = KeyProtectionMode.valueOf(modeName);
        } catch (IllegalArgumentException e) {
            mode = KeyProtectionMode.SOFTWARE_ONLY;
        }

        return new KeyProtectionConfig(mode, Paths.get(fallbackDirStr));
    }

    private static Path getDefaultFallbackDir() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".aegisvault", "keys");
    }
}
