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
package com.aegisvault.sync;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class SyncConfig {

    public enum ProviderType {
        LOCAL_FOLDER,
        WEBDAV
    }

    public enum SyncFrequency {
        MANUAL,
        ON_CLOSE,
        PERIODIC
    }

    public enum ConflictResolution {
        KEEP_LOCAL,
        KEEP_REMOTE,
        ASK_USER
    }

    private boolean enabled;
    private ProviderType providerType;
    private String remotePath;
    private SyncFrequency frequency;
    private ConflictResolution conflictResolution;
    private long lastSyncTimestamp;
    private int periodicIntervalMinutes;

    public SyncConfig() {
        this.enabled = false;
        this.providerType = ProviderType.LOCAL_FOLDER;
        this.remotePath = "";
        this.frequency = SyncFrequency.MANUAL;
        this.conflictResolution = ConflictResolution.ASK_USER;
        this.lastSyncTimestamp = 0;
        this.periodicIntervalMinutes = 30;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public SyncFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(SyncFrequency frequency) {
        this.frequency = frequency;
    }

    public ConflictResolution getConflictResolution() {
        return conflictResolution;
    }

    public void setConflictResolution(ConflictResolution conflictResolution) {
        this.conflictResolution = conflictResolution;
    }

    public long getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }

    public void setLastSyncTimestamp(long lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }

    public int getPeriodicIntervalMinutes() {
        return periodicIntervalMinutes;
    }

    public void setPeriodicIntervalMinutes(int periodicIntervalMinutes) {
        this.periodicIntervalMinutes = periodicIntervalMinutes;
    }

    public void save(String vaultId) {
        Preferences prefs = Preferences.userNodeForPackage(SyncConfig.class).node(sanitize(vaultId));
        prefs.putBoolean("enabled", enabled);
        prefs.put("providerType", providerType.name());
        prefs.put("remotePath", remotePath);
        prefs.put("frequency", frequency.name());
        prefs.put("conflictResolution", conflictResolution.name());
        prefs.putLong("lastSyncTimestamp", lastSyncTimestamp);
        prefs.putInt("periodicIntervalMinutes", periodicIntervalMinutes);
    }

    public static SyncConfig load(String vaultId) {
        Preferences prefs = Preferences.userNodeForPackage(SyncConfig.class).node(sanitize(vaultId));
        SyncConfig config = new SyncConfig();
        config.setEnabled(prefs.getBoolean("enabled", false));

        try {
            config.setProviderType(ProviderType.valueOf(prefs.get("providerType", "LOCAL_FOLDER")));
        } catch (IllegalArgumentException e) {
            config.setProviderType(ProviderType.LOCAL_FOLDER);
        }

        config.setRemotePath(prefs.get("remotePath", ""));

        try {
            config.setFrequency(SyncFrequency.valueOf(prefs.get("frequency", "MANUAL")));
        } catch (IllegalArgumentException e) {
            config.setFrequency(SyncFrequency.MANUAL);
        }

        try {
            config.setConflictResolution(ConflictResolution.valueOf(prefs.get("conflictResolution", "ASK_USER")));
        } catch (IllegalArgumentException e) {
            config.setConflictResolution(ConflictResolution.ASK_USER);
        }

        config.setLastSyncTimestamp(prefs.getLong("lastSyncTimestamp", 0));
        config.setPeriodicIntervalMinutes(prefs.getInt("periodicIntervalMinutes", 30));

        return config;
    }

    private static String sanitize(String vaultId) {
        return vaultId.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
