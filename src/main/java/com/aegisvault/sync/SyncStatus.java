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

public class SyncStatus {

    public enum State {
        IDLE,
        SYNCING,
        CONFLICT,
        ERROR,
        UP_TO_DATE
    }

    private State state;
    private long lastSyncTime;
    private long bytesTransferred;
    private String errorMessage;
    private String conflictDetails;
    private double progress;

    public SyncStatus() {
        this.state = State.IDLE;
        this.lastSyncTime = 0;
        this.bytesTransferred = 0;
        this.progress = 0.0;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

    public void setBytesTransferred(long bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getConflictDetails() {
        return conflictDetails;
    }

    public void setConflictDetails(String conflictDetails) {
        this.conflictDetails = conflictDetails;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getStatusText() {
        return switch (state) {
            case IDLE -> "Not synced";
            case SYNCING -> String.format("Syncing... %.0f%%", progress * 100);
            case CONFLICT -> "Sync conflict: " + (conflictDetails != null ? conflictDetails : "");
            case ERROR -> "Sync error: " + (errorMessage != null ? errorMessage : "Unknown");
            case UP_TO_DATE -> "Up to date";
        };
    }
}
