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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnterpriseConfig {

    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 8;
    private static final int DEFAULT_MAX_AUTO_LOCK_MINUTES = 60;
    private static final int DEFAULT_AUDIT_RETENTION_DAYS = 365;

    private boolean managed;
    private String configSource;
    private int minPasswordLength;
    private int maxAutoLockMinutes;
    private boolean syncAllowed;
    private boolean experimentalCiphersAllowed;
    private int auditRetentionDays;
    private boolean auditRequired;
    private boolean keyProtectionRequired;
    private String allowedCiphers;
    private String defaultVaultPath;
    private final Map<String, Boolean> lockedSettings;
    private final Map<String, String> customProperties;

    public EnterpriseConfig() {
        this.managed = false;
        this.configSource = "defaults";
        this.minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;
        this.maxAutoLockMinutes = DEFAULT_MAX_AUTO_LOCK_MINUTES;
        this.syncAllowed = true;
        this.experimentalCiphersAllowed = true;
        this.auditRetentionDays = DEFAULT_AUDIT_RETENTION_DAYS;
        this.auditRequired = false;
        this.keyProtectionRequired = false;
        this.allowedCiphers = "AES-256-GCM";
        this.defaultVaultPath = "";
        this.lockedSettings = new HashMap<>();
        this.customProperties = new HashMap<>();
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    public String getConfigSource() {
        return configSource;
    }

    public void setConfigSource(String configSource) {
        this.configSource = configSource;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }

    public void setMinPasswordLength(int minPasswordLength) {
        this.minPasswordLength = Math.max(1, minPasswordLength);
    }

    public int getMaxAutoLockMinutes() {
        return maxAutoLockMinutes;
    }

    public void setMaxAutoLockMinutes(int maxAutoLockMinutes) {
        this.maxAutoLockMinutes = Math.max(1, maxAutoLockMinutes);
    }

    public boolean isSyncAllowed() {
        return syncAllowed;
    }

    public void setSyncAllowed(boolean syncAllowed) {
        this.syncAllowed = syncAllowed;
    }

    public boolean isExperimentalCiphersAllowed() {
        return experimentalCiphersAllowed;
    }

    public void setExperimentalCiphersAllowed(boolean experimentalCiphersAllowed) {
        this.experimentalCiphersAllowed = experimentalCiphersAllowed;
    }

    public int getAuditRetentionDays() {
        return auditRetentionDays;
    }

    public void setAuditRetentionDays(int auditRetentionDays) {
        this.auditRetentionDays = auditRetentionDays;
    }

    public boolean isAuditRequired() {
        return auditRequired;
    }

    public void setAuditRequired(boolean auditRequired) {
        this.auditRequired = auditRequired;
    }

    public boolean isKeyProtectionRequired() {
        return keyProtectionRequired;
    }

    public void setKeyProtectionRequired(boolean keyProtectionRequired) {
        this.keyProtectionRequired = keyProtectionRequired;
    }

    public String getAllowedCiphers() {
        return allowedCiphers;
    }

    public void setAllowedCiphers(String allowedCiphers) {
        this.allowedCiphers = allowedCiphers;
    }

    public String getDefaultVaultPath() {
        return defaultVaultPath;
    }

    public void setDefaultVaultPath(String defaultVaultPath) {
        this.defaultVaultPath = defaultVaultPath;
    }

    public boolean isPolicyLocked(String setting) {
        return lockedSettings.getOrDefault(setting, false);
    }

    public void lockSetting(String setting) {
        lockedSettings.put(setting, true);
    }

    public Set<String> getLockedSettings() {
        return Collections.unmodifiableSet(lockedSettings.keySet());
    }

    public void setCustomProperty(String key, String value) {
        customProperties.put(key, value);
    }

    public String getCustomProperty(String key, String defaultValue) {
        return customProperties.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getCustomProperties() {
        return Collections.unmodifiableMap(customProperties);
    }

    public boolean isPasswordAcceptable(int passwordLength) {
        return passwordLength >= minPasswordLength;
    }

    public boolean isAutoLockTimeoutAcceptable(int minutes) {
        return minutes <= maxAutoLockMinutes;
    }
}
