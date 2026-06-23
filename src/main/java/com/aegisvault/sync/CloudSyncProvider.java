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
import java.nio.file.Path;
import java.util.List;

public interface CloudSyncProvider {

    void upload(Path localVault, String remoteId) throws IOException;

    void download(String remoteId, Path localPath) throws IOException;

    long getLastModified(String remoteId) throws IOException;

    void delete(String remoteId) throws IOException;

    List<String> listRemoteVaults() throws IOException;

    boolean testConnection() throws IOException;

    String getProviderName();
}
