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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class MultiVaultManager {

    private static final String PREFS_VAULT_COUNT = "registeredVaultCount";
    private static final String PREFS_VAULT_PREFIX = "vault_";

    private final Preferences prefs;
    private final List<VaultRegistration> registrations;

    public MultiVaultManager() {
        this.prefs = Preferences.userNodeForPackage(MultiVaultManager.class);
        this.registrations = new ArrayList<>();
        loadRegistrations();
    }

    public void registerVault(Path vaultPath, String label) {
        String normalizedPath = vaultPath.toAbsolutePath().normalize().toString();

        for (VaultRegistration reg : registrations) {
            if (reg.path().equals(normalizedPath)) {
                registrations.remove(reg);
                break;
            }
        }

        registrations.add(new VaultRegistration(
                normalizedPath,
                label,
                System.currentTimeMillis()
        ));

        saveRegistrations();
    }

    public void unregisterVault(String vaultPath) {
        registrations.removeIf(reg -> reg.path().equals(vaultPath));
        saveRegistrations();
    }

    public List<VaultRegistration> listRegisteredVaults() {
        return Collections.unmodifiableList(new ArrayList<>(registrations));
    }

    public VaultStatus getVaultStatus(String vaultPath) {
        Path path = Path.of(vaultPath);

        if (!Files.exists(path)) {
            return new VaultStatus(vaultPath, false, false, -1, "File not found");
        }

        boolean readable = Files.isReadable(path);
        long size;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            size = -1;
        }

        String status = readable ? "OK" : "Not readable";
        return new VaultStatus(vaultPath, true, readable, size, status);
    }

    public List<VaultStatus> getAllVaultStatuses() {
        List<VaultStatus> statuses = new ArrayList<>();
        for (VaultRegistration reg : registrations) {
            statuses.add(getVaultStatus(reg.path()));
        }
        return statuses;
    }

    private void loadRegistrations() {
        int count = prefs.getInt(PREFS_VAULT_COUNT, 0);
        for (int i = 0; i < count; i++) {
            String path = prefs.get(PREFS_VAULT_PREFIX + i + "_path", null);
            String label = prefs.get(PREFS_VAULT_PREFIX + i + "_label", "");
            long registeredAt = prefs.getLong(PREFS_VAULT_PREFIX + i + "_registeredAt", 0);

            if (path != null) {
                registrations.add(new VaultRegistration(path, label, registeredAt));
            }
        }
    }

    private void saveRegistrations() {
        int oldCount = prefs.getInt(PREFS_VAULT_COUNT, 0);
        for (int i = 0; i < oldCount; i++) {
            prefs.remove(PREFS_VAULT_PREFIX + i + "_path");
            prefs.remove(PREFS_VAULT_PREFIX + i + "_label");
            prefs.remove(PREFS_VAULT_PREFIX + i + "_registeredAt");
        }

        prefs.putInt(PREFS_VAULT_COUNT, registrations.size());
        for (int i = 0; i < registrations.size(); i++) {
            VaultRegistration reg = registrations.get(i);
            prefs.put(PREFS_VAULT_PREFIX + i + "_path", reg.path());
            prefs.put(PREFS_VAULT_PREFIX + i + "_label", reg.label());
            prefs.putLong(PREFS_VAULT_PREFIX + i + "_registeredAt", reg.registeredAt());
        }
    }

    public record VaultRegistration(String path, String label, long registeredAt) {
    }

    public record VaultStatus(String path, boolean exists, boolean readable, long sizeBytes, String status) {
        public String formattedSize() {
            if (sizeBytes < 0) return "N/A";
            if (sizeBytes < 1024) return sizeBytes + " B";
            if (sizeBytes < 1024 * 1024) return String.format("%.1f KB", sizeBytes / 1024.0);
            if (sizeBytes < 1024 * 1024 * 1024) return String.format("%.1f MB", sizeBytes / (1024.0 * 1024));
            return String.format("%.1f GB", sizeBytes / (1024.0 * 1024 * 1024));
        }
    }
}
