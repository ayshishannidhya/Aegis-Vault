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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebDavSyncProvider implements CloudSyncProvider {

    private static final Pattern HREF_PATTERN = Pattern.compile("<D:href>([^<]+)</D:href>");
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final String baseUrl;
    private final String username;
    private final char[] password;
    private final HttpClient httpClient;

    public WebDavSyncProvider(String baseUrl, String username, char[] password) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.username = username;
        this.password = password != null ? password.clone() : new char[0];
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    @Override
    public void upload(Path localVault, String remoteId) throws IOException {
        URI uri = URI.create(baseUrl + remoteId);
        byte[] fileBytes = Files.readAllBytes(localVault);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .header("Content-Type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .timeout(Duration.ofMinutes(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IOException("Upload failed with status " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Upload interrupted", e);
        }
    }

    @Override
    public void download(String remoteId, Path localPath) throws IOException {
        URI uri = URI.create(baseUrl + remoteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .GET()
                .timeout(Duration.ofMinutes(10))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 404) {
                throw new IOException("Remote vault not found: " + remoteId);
            }
            if (response.statusCode() >= 400) {
                throw new IOException("Download failed with status " + response.statusCode());
            }
            Files.write(localPath, response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrupted", e);
        }
    }

    @Override
    public long getLastModified(String remoteId) throws IOException {
        URI uri = URI.create(baseUrl + remoteId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .timeout(TIMEOUT)
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == 404) {
                return -1;
            }
            return response.headers()
                    .firstValue("Last-Modified")
                    .map(this::parseHttpDate)
                    .orElse(System.currentTimeMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    @Override
    public void delete(String remoteId) throws IOException {
        URI uri = URI.create(baseUrl + remoteId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .DELETE()
                .timeout(TIMEOUT)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400 && response.statusCode() != 404) {
                throw new IOException("Delete failed with status " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Delete interrupted", e);
        }
    }

    @Override
    public List<String> listRemoteVaults() throws IOException {
        URI uri = URI.create(baseUrl);
        String propfindBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<D:propfind xmlns:D=\"DAV:\">"
                + "<D:prop><D:displayname/><D:getlastmodified/></D:prop>"
                + "</D:propfind>";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .header("Content-Type", "application/xml")
                .header("Depth", "1")
                .method("PROPFIND", HttpRequest.BodyPublishers.ofString(propfindBody))
                .timeout(TIMEOUT)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IOException("PROPFIND failed with status " + response.statusCode());
            }
            return parseVaultList(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("List interrupted", e);
        }
    }

    @Override
    public boolean testConnection() throws IOException {
        URI uri = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", buildAuthHeader())
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .timeout(TIMEOUT)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() < 400;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection test interrupted", e);
        }
    }

    @Override
    public String getProviderName() {
        return "WebDAV";
    }

    private String buildAuthHeader() {
        String credentials = username + ":" + new String(password);
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private List<String> parseVaultList(String xmlResponse) {
        List<String> vaults = new ArrayList<>();
        Matcher matcher = HREF_PATTERN.matcher(xmlResponse);
        while (matcher.find()) {
            String href = matcher.group(1);
            if (href.endsWith(".avj")) {
                int lastSlash = href.lastIndexOf('/');
                String fileName = lastSlash >= 0 ? href.substring(lastSlash + 1) : href;
                vaults.add(fileName);
            }
        }
        return vaults;
    }

    private long parseHttpDate(String dateStr) {
        try {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
            java.time.ZonedDateTime zdt = java.time.ZonedDateTime.parse(dateStr, formatter);
            return zdt.toInstant().toEpochMilli();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
