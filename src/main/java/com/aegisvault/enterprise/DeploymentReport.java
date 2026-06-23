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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeploymentReport {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneId.systemDefault());

    private final EnterpriseConfig enterpriseConfig;
    private final MultiVaultManager vaultManager;

    public DeploymentReport(EnterpriseConfig enterpriseConfig, MultiVaultManager vaultManager) {
        this.enterpriseConfig = enterpriseConfig;
        this.vaultManager = vaultManager;
    }

    public String generateJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        json.append("  \"reportTimestamp\": \"").append(TIMESTAMP_FORMAT.format(Instant.now())).append("\",\n");
        json.append("  \"version\": \"0.1.0\",\n");

        appendSystemInfo(json);
        appendConfigInfo(json);
        appendVaultInventory(json);

        json.append("}\n");
        return json.toString();
    }

    public Map<String, String> getSystemInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("OS Name", System.getProperty("os.name"));
        info.put("OS Version", System.getProperty("os.version"));
        info.put("OS Architecture", System.getProperty("os.arch"));
        info.put("Java Version", System.getProperty("java.version"));
        info.put("Java Vendor", System.getProperty("java.vendor"));
        info.put("Java Home", System.getProperty("java.home"));
        info.put("User Name", System.getProperty("user.name"));
        info.put("User Home", System.getProperty("user.home"));

        try {
            info.put("Hostname", java.net.InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            info.put("Hostname", "unknown");
        }

        info.put("Max Memory", formatBytes(Runtime.getRuntime().maxMemory()));
        info.put("Available Processors", String.valueOf(Runtime.getRuntime().availableProcessors()));

        return info;
    }

    private void appendSystemInfo(StringBuilder json) {
        json.append("  \"system\": {\n");
        json.append("    \"osName\": \"").append(escape(System.getProperty("os.name", ""))).append("\",\n");
        json.append("    \"osVersion\": \"").append(escape(System.getProperty("os.version", ""))).append("\",\n");
        json.append("    \"osArch\": \"").append(escape(System.getProperty("os.arch", ""))).append("\",\n");
        json.append("    \"javaVersion\": \"").append(escape(System.getProperty("java.version", ""))).append("\",\n");
        json.append("    \"javaVendor\": \"").append(escape(System.getProperty("java.vendor", ""))).append("\",\n");
        json.append("    \"maxMemoryBytes\": ").append(Runtime.getRuntime().maxMemory()).append(",\n");
        json.append("    \"processors\": ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        json.append("  },\n");
    }

    private void appendConfigInfo(StringBuilder json) {
        json.append("  \"configuration\": {\n");
        json.append("    \"managed\": ").append(enterpriseConfig.isManaged()).append(",\n");
        json.append("    \"configSource\": \"").append(escape(enterpriseConfig.getConfigSource())).append("\",\n");
        json.append("    \"minPasswordLength\": ").append(enterpriseConfig.getMinPasswordLength()).append(",\n");
        json.append("    \"maxAutoLockMinutes\": ").append(enterpriseConfig.getMaxAutoLockMinutes()).append(",\n");
        json.append("    \"syncAllowed\": ").append(enterpriseConfig.isSyncAllowed()).append(",\n");
        json.append("    \"experimentalCiphersAllowed\": ").append(enterpriseConfig.isExperimentalCiphersAllowed()).append(",\n");
        json.append("    \"auditRequired\": ").append(enterpriseConfig.isAuditRequired()).append(",\n");
        json.append("    \"keyProtectionRequired\": ").append(enterpriseConfig.isKeyProtectionRequired()).append(",\n");
        json.append("    \"allowedCiphers\": \"").append(escape(enterpriseConfig.getAllowedCiphers())).append("\",\n");

        json.append("    \"lockedSettings\": [");
        String[] locked = enterpriseConfig.getLockedSettings().toArray(new String[0]);
        for (int i = 0; i < locked.length; i++) {
            json.append("\"").append(escape(locked[i])).append("\"");
            if (i < locked.length - 1) json.append(", ");
        }
        json.append("]\n");

        json.append("  },\n");
    }

    private void appendVaultInventory(StringBuilder json) {
        json.append("  \"vaultInventory\": [\n");

        List<MultiVaultManager.VaultStatus> statuses = vaultManager.getAllVaultStatuses();
        List<MultiVaultManager.VaultRegistration> registrations = vaultManager.listRegisteredVaults();

        for (int i = 0; i < statuses.size(); i++) {
            MultiVaultManager.VaultStatus status = statuses.get(i);
            MultiVaultManager.VaultRegistration reg = i < registrations.size() ? registrations.get(i) : null;

            json.append("    {\n");
            json.append("      \"path\": \"").append(escape(status.path())).append("\",\n");
            json.append("      \"label\": \"").append(escape(reg != null ? reg.label() : "")).append("\",\n");
            json.append("      \"exists\": ").append(status.exists()).append(",\n");
            json.append("      \"readable\": ").append(status.readable()).append(",\n");
            json.append("      \"sizeBytes\": ").append(status.sizeBytes()).append(",\n");
            json.append("      \"status\": \"").append(escape(status.status())).append("\"\n");
            json.append("    }");
            if (i < statuses.size() - 1) json.append(",");
            json.append("\n");
        }

        json.append("  ]\n");
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
