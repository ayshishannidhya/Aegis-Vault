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
package com.aegisvault.audit;

public enum AuditEventType {

    VAULT_CREATED("Vault Created", Severity.INFO),
    VAULT_OPENED("Vault Opened", Severity.INFO),
    VAULT_CLOSED("Vault Closed", Severity.INFO),
    VAULT_AUTO_LOCKED("Vault Auto-Locked", Severity.WARNING),

    FILE_IMPORTED("File Imported", Severity.INFO),
    FILE_EXPORTED("File Exported", Severity.WARNING),
    FILE_DELETED("File Deleted", Severity.WARNING),
    FILE_RENAMED("File Renamed", Severity.INFO),
    FILE_READ("File Read", Severity.INFO),
    FILE_WRITTEN("File Written", Severity.INFO),

    FOLDER_CREATED("Folder Created", Severity.INFO),
    FOLDER_DELETED("Folder Deleted", Severity.WARNING),

    PASSWORD_CHANGED("Password Changed", Severity.SECURITY),
    FAILED_LOGIN("Failed Login Attempt", Severity.SECURITY),

    SYNC_UPLOAD("Sync Upload", Severity.INFO),
    SYNC_DOWNLOAD("Sync Download", Severity.INFO),
    SYNC_CONFLICT("Sync Conflict", Severity.WARNING),
    SYNC_ERROR("Sync Error", Severity.WARNING),

    BACKUP_CREATED("Backup Created", Severity.INFO),

    KEY_PROTECTION_ENABLED("Key Protection Enabled", Severity.SECURITY),
    KEY_PROTECTION_DISABLED("Key Protection Disabled", Severity.SECURITY),

    ENTERPRISE_POLICY_APPLIED("Enterprise Policy Applied", Severity.INFO),
    ENTERPRISE_POLICY_VIOLATION("Enterprise Policy Violation", Severity.SECURITY);

    public enum Severity {
        INFO,
        WARNING,
        SECURITY
    }

    private final String displayName;
    private final Severity severity;

    AuditEventType(String displayName, Severity severity) {
        this.displayName = displayName;
        this.severity = severity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Severity getSeverity() {
        return severity;
    }
}
