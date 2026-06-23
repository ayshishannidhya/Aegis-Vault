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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SyncEngine {

    private final ExecutorService executor;
    private final SyncStatus status;
    private Consumer<SyncStatus> statusCallback;

    public SyncEngine() {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "VaultSync");
            t.setDaemon(true);
            return t;
        });
        this.status = new SyncStatus();
    }

    public void setStatusCallback(Consumer<SyncStatus> callback) {
        this.statusCallback = callback;
    }

    public SyncStatus getStatus() {
        return status;
    }

    public CompletableFuture<SyncResult> syncNow(Path vaultPath, SyncConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return performSync(vaultPath, config);
            } catch (Exception e) {
                updateStatus(SyncStatus.State.ERROR, e.getMessage());
                return new SyncResult(false, e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<SyncResult> uploadOnly(Path vaultPath, SyncConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloudSyncProvider provider = createProvider(config);
                String remoteId = vaultPath.getFileName().toString();

                updateStatus(SyncStatus.State.SYNCING, null);
                status.setProgress(0.0);
                notifyCallback();

                provider.upload(vaultPath, remoteId);

                config.setLastSyncTimestamp(System.currentTimeMillis());
                updateStatus(SyncStatus.State.UP_TO_DATE, null);
                status.setBytesTransferred(Files.size(vaultPath));
                status.setLastSyncTime(System.currentTimeMillis());
                notifyCallback();

                return new SyncResult(true, "Upload complete");
            } catch (Exception e) {
                updateStatus(SyncStatus.State.ERROR, e.getMessage());
                return new SyncResult(false, e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<SyncResult> downloadOnly(Path vaultPath, SyncConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloudSyncProvider provider = createProvider(config);
                String remoteId = vaultPath.getFileName().toString();

                updateStatus(SyncStatus.State.SYNCING, null);
                notifyCallback();

                provider.download(remoteId, vaultPath);

                config.setLastSyncTimestamp(System.currentTimeMillis());
                updateStatus(SyncStatus.State.UP_TO_DATE, null);
                status.setBytesTransferred(Files.size(vaultPath));
                status.setLastSyncTime(System.currentTimeMillis());
                notifyCallback();

                return new SyncResult(true, "Download complete");
            } catch (Exception e) {
                updateStatus(SyncStatus.State.ERROR, e.getMessage());
                return new SyncResult(false, e.getMessage());
            }
        }, executor);
    }

    private SyncResult performSync(Path vaultPath, SyncConfig config) throws IOException {
        CloudSyncProvider provider = createProvider(config);
        String remoteId = vaultPath.getFileName().toString();

        updateStatus(SyncStatus.State.SYNCING, null);
        status.setProgress(0.0);
        notifyCallback();

        long remoteModified = provider.getLastModified(remoteId);
        long localModified = Files.exists(vaultPath) ? Files.getLastModifiedTime(vaultPath).toMillis() : -1;

        status.setProgress(0.25);
        notifyCallback();

        if (remoteModified == -1) {
            provider.upload(vaultPath, remoteId);
            config.setLastSyncTimestamp(System.currentTimeMillis());
            updateStatus(SyncStatus.State.UP_TO_DATE, null);
            status.setBytesTransferred(Files.size(vaultPath));
            status.setLastSyncTime(System.currentTimeMillis());
            notifyCallback();
            return new SyncResult(true, "Uploaded (first sync)");
        }

        boolean localChanged = localModified > config.getLastSyncTimestamp();
        boolean remoteChanged = remoteModified > config.getLastSyncTimestamp();

        status.setProgress(0.5);
        notifyCallback();

        if (localChanged && remoteChanged) {
            return handleConflict(vaultPath, config, provider, remoteId);
        }

        if (localChanged) {
            provider.upload(vaultPath, remoteId);
            config.setLastSyncTimestamp(System.currentTimeMillis());
            updateStatus(SyncStatus.State.UP_TO_DATE, null);
            status.setBytesTransferred(Files.size(vaultPath));
            status.setLastSyncTime(System.currentTimeMillis());
            notifyCallback();
            return new SyncResult(true, "Uploaded local changes");
        }

        if (remoteChanged) {
            provider.download(remoteId, vaultPath);
            config.setLastSyncTimestamp(System.currentTimeMillis());
            updateStatus(SyncStatus.State.UP_TO_DATE, null);
            status.setLastSyncTime(System.currentTimeMillis());
            notifyCallback();
            return new SyncResult(true, "Downloaded remote changes");
        }

        updateStatus(SyncStatus.State.UP_TO_DATE, null);
        status.setLastSyncTime(System.currentTimeMillis());
        notifyCallback();
        return new SyncResult(true, "Already up to date");
    }

    private SyncResult handleConflict(Path vaultPath, SyncConfig config,
                                       CloudSyncProvider provider, String remoteId) throws IOException {
        switch (config.getConflictResolution()) {
            case KEEP_LOCAL -> {
                provider.upload(vaultPath, remoteId);
                config.setLastSyncTimestamp(System.currentTimeMillis());
                updateStatus(SyncStatus.State.UP_TO_DATE, null);
                status.setLastSyncTime(System.currentTimeMillis());
                notifyCallback();
                return new SyncResult(true, "Conflict resolved: kept local version");
            }
            case KEEP_REMOTE -> {
                provider.download(remoteId, vaultPath);
                config.setLastSyncTimestamp(System.currentTimeMillis());
                updateStatus(SyncStatus.State.UP_TO_DATE, null);
                status.setLastSyncTime(System.currentTimeMillis());
                notifyCallback();
                return new SyncResult(true, "Conflict resolved: kept remote version");
            }
            default -> {
                updateStatus(SyncStatus.State.CONFLICT, "Both local and remote have changed since last sync");
                notifyCallback();
                return new SyncResult(false, "Conflict detected - user action required");
            }
        }
    }

    public static CloudSyncProvider createProvider(SyncConfig config) {
        return switch (config.getProviderType()) {
            case LOCAL_FOLDER -> new LocalFolderSyncProvider(Path.of(config.getRemotePath()));
            case WEBDAV -> {
                String[] parts = config.getRemotePath().split("\\|", 3);
                String url = parts[0];
                String user = parts.length > 1 ? parts[1] : "";
                char[] pass = parts.length > 2 ? parts[2].toCharArray() : new char[0];
                yield new WebDavSyncProvider(url, user, pass);
            }
        };
    }

    public CompletableFuture<Boolean> testConnection(SyncConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloudSyncProvider provider = createProvider(config);
                return provider.testConnection();
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }

    public void shutdown() {
        executor.shutdown();
    }

    private void updateStatus(SyncStatus.State state, String message) {
        status.setState(state);
        if (state == SyncStatus.State.ERROR) {
            status.setErrorMessage(message);
        } else if (state == SyncStatus.State.CONFLICT) {
            status.setConflictDetails(message);
        }
        status.setProgress(state == SyncStatus.State.UP_TO_DATE ? 1.0 : status.getProgress());
    }

    private void notifyCallback() {
        if (statusCallback != null) {
            statusCallback.accept(status);
        }
    }

    public static String computeFileHash(Path filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream is = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hash = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 not available", e);
        }
    }

    public record SyncResult(boolean success, String message) {
    }
}
