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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class LocalFolderSyncProvider implements CloudSyncProvider {

    private final Path syncFolder;

    public LocalFolderSyncProvider(Path syncFolder) {
        this.syncFolder = syncFolder;
    }

    @Override
    public void upload(Path localVault, String remoteId) throws IOException {
        Files.createDirectories(syncFolder);
        Path remotePath = syncFolder.resolve(remoteId);
        Files.copy(localVault, remotePath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void download(String remoteId, Path localPath) throws IOException {
        Path remotePath = syncFolder.resolve(remoteId);
        if (!Files.exists(remotePath)) {
            throw new IOException("Remote vault not found: " + remoteId);
        }
        Files.copy(remotePath, localPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public long getLastModified(String remoteId) throws IOException {
        Path remotePath = syncFolder.resolve(remoteId);
        if (!Files.exists(remotePath)) {
            return -1;
        }
        return Files.getLastModifiedTime(remotePath).toMillis();
    }

    @Override
    public void delete(String remoteId) throws IOException {
        Path remotePath = syncFolder.resolve(remoteId);
        Files.deleteIfExists(remotePath);
    }

    @Override
    public List<String> listRemoteVaults() throws IOException {
        List<String> vaults = new ArrayList<>();
        if (!Files.exists(syncFolder)) {
            return vaults;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(syncFolder, "*.avj")) {
            for (Path entry : stream) {
                vaults.add(entry.getFileName().toString());
            }
        }
        return vaults;
    }

    @Override
    public boolean testConnection() throws IOException {
        if (!Files.exists(syncFolder)) {
            Files.createDirectories(syncFolder);
        }
        return Files.isWritable(syncFolder);
    }

    @Override
    public String getProviderName() {
        return "Local Folder";
    }
}
