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

import com.aegisvault.exception.CryptoException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public final class HardwareKeyProtection {

    private static final String ALIAS_PREFIX = "aegisvault_kwk_";
    private static final String KEY_ALGORITHM = "AES";
    private static final String FALLBACK_KEYSTORE_NAME = "aegisvault_keys.p12";

    private HardwareKeyProtection() {
    }

    public static boolean isOsKeystoreAvailable() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        try {
            if (osName.contains("win")) {
                KeyStore ks = KeyStore.getInstance("Windows-MY");
                ks.load(null, null);
                return true;
            } else if (osName.contains("mac")) {
                KeyStore ks = KeyStore.getInstance("KeychainStore");
                ks.load(null, null);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static String getKeystoreType() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("win")) {
            return "Windows-MY";
        } else if (osName.contains("mac")) {
            return "KeychainStore";
        }
        return "PKCS12";
    }

    public static void storeKeyWrappingKey(String vaultId, byte[] keyData, Path fallbackDir) {
        if (keyData == null || keyData.length != SecureRandomProvider.KEY_SIZE_BYTES) {
            throw new IllegalArgumentException("Key data must be " + SecureRandomProvider.KEY_SIZE_BYTES + " bytes");
        }

        String alias = ALIAS_PREFIX + sanitizeAlias(vaultId);
        SecretKey secretKey = new SecretKeySpec(keyData, KEY_ALGORITHM);
        KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(new char[0]);

        try {
            if (isOsKeystoreAvailable()) {
                storeInOsKeystore(alias, entry, protection);
            } else {
                storeInFallbackKeystore(alias, entry, protection, fallbackDir);
            }
        } catch (Exception e) {
            storeInFallbackKeystore(alias, entry, protection, fallbackDir);
        }
    }

    public static byte[] retrieveKeyWrappingKey(String vaultId, Path fallbackDir) {
        String alias = ALIAS_PREFIX + sanitizeAlias(vaultId);
        KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(new char[0]);

        try {
            if (isOsKeystoreAvailable()) {
                byte[] key = retrieveFromOsKeystore(alias, protection);
                if (key != null) {
                    return key;
                }
            }
        } catch (Exception ignored) {
        }

        return retrieveFromFallbackKeystore(alias, protection, fallbackDir);
    }

    public static boolean hasKeyWrappingKey(String vaultId, Path fallbackDir) {
        String alias = ALIAS_PREFIX + sanitizeAlias(vaultId);

        try {
            if (isOsKeystoreAvailable()) {
                KeyStore ks = KeyStore.getInstance(getKeystoreType());
                ks.load(null, null);
                if (ks.containsAlias(alias)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        Path fallbackPath = fallbackDir.resolve(FALLBACK_KEYSTORE_NAME);
        if (Files.exists(fallbackPath)) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                try (InputStream is = Files.newInputStream(fallbackPath)) {
                    ks.load(is, new char[0]);
                }
                return ks.containsAlias(alias);
            } catch (Exception ignored) {
            }
        }

        return false;
    }

    public static void deleteKeyWrappingKey(String vaultId, Path fallbackDir) {
        String alias = ALIAS_PREFIX + sanitizeAlias(vaultId);

        try {
            if (isOsKeystoreAvailable()) {
                KeyStore ks = KeyStore.getInstance(getKeystoreType());
                ks.load(null, null);
                if (ks.containsAlias(alias)) {
                    ks.deleteEntry(alias);
                    ks.store(null, null);
                }
            }
        } catch (Exception ignored) {
        }

        Path fallbackPath = fallbackDir.resolve(FALLBACK_KEYSTORE_NAME);
        if (Files.exists(fallbackPath)) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                try (InputStream is = Files.newInputStream(fallbackPath)) {
                    ks.load(is, new char[0]);
                }
                if (ks.containsAlias(alias)) {
                    ks.deleteEntry(alias);
                    try (OutputStream os = Files.newOutputStream(fallbackPath)) {
                        ks.store(os, new char[0]);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static List<String> listStoredVaults(Path fallbackDir) {
        List<String> vaultIds = new ArrayList<>();

        try {
            if (isOsKeystoreAvailable()) {
                KeyStore ks = KeyStore.getInstance(getKeystoreType());
                ks.load(null, null);
                addAliases(ks, vaultIds);
            }
        } catch (Exception ignored) {
        }

        Path fallbackPath = fallbackDir.resolve(FALLBACK_KEYSTORE_NAME);
        if (Files.exists(fallbackPath)) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                try (InputStream is = Files.newInputStream(fallbackPath)) {
                    ks.load(is, new char[0]);
                }
                addAliases(ks, vaultIds);
            } catch (Exception ignored) {
            }
        }

        return Collections.unmodifiableList(vaultIds);
    }

    private static void addAliases(KeyStore ks, List<String> vaultIds) throws java.security.KeyStoreException {
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (alias.startsWith(ALIAS_PREFIX)) {
                vaultIds.add(alias.substring(ALIAS_PREFIX.length()));
            }
        }
    }

    private static void storeInOsKeystore(String alias, KeyStore.SecretKeyEntry entry,
                                           KeyStore.ProtectionParameter protection) {
        try {
            KeyStore ks = KeyStore.getInstance(getKeystoreType());
            ks.load(null, null);
            ks.setEntry(alias, entry, protection);
            ks.store(null, null);
        } catch (Exception e) {
            throw new CryptoException("Failed to store key in OS keystore", e);
        }
    }

    private static void storeInFallbackKeystore(String alias, KeyStore.SecretKeyEntry entry,
                                                 KeyStore.ProtectionParameter protection,
                                                 Path fallbackDir) {
        try {
            Files.createDirectories(fallbackDir);
            Path fallbackPath = fallbackDir.resolve(FALLBACK_KEYSTORE_NAME);

            KeyStore ks = KeyStore.getInstance("PKCS12");
            if (Files.exists(fallbackPath)) {
                try (InputStream is = Files.newInputStream(fallbackPath)) {
                    ks.load(is, new char[0]);
                }
            } else {
                ks.load(null, new char[0]);
            }

            ks.setEntry(alias, entry, protection);

            try (OutputStream os = Files.newOutputStream(fallbackPath)) {
                ks.store(os, new char[0]);
            }
        } catch (Exception e) {
            throw new CryptoException("Failed to store key in fallback keystore", e);
        }
    }

    private static byte[] retrieveFromOsKeystore(String alias, KeyStore.ProtectionParameter protection) {
        try {
            KeyStore ks = KeyStore.getInstance(getKeystoreType());
            ks.load(null, null);
            KeyStore.Entry entry = ks.getEntry(alias, protection);
            if (entry instanceof KeyStore.SecretKeyEntry secretKeyEntry) {
                return secretKeyEntry.getSecretKey().getEncoded();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static byte[] retrieveFromFallbackKeystore(String alias, KeyStore.ProtectionParameter protection,
                                                        Path fallbackDir) {
        Path fallbackPath = fallbackDir.resolve(FALLBACK_KEYSTORE_NAME);
        if (!Files.exists(fallbackPath)) {
            return null;
        }

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = Files.newInputStream(fallbackPath)) {
                ks.load(is, new char[0]);
            }
            KeyStore.Entry entry = ks.getEntry(alias, protection);
            if (entry instanceof KeyStore.SecretKeyEntry secretKeyEntry) {
                return secretKeyEntry.getSecretKey().getEncoded();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String sanitizeAlias(String vaultId) {
        return vaultId.replaceAll("[^a-zA-Z0-9_\\-.]", "_");
    }
}
