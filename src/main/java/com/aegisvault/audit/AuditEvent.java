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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AuditEvent {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final long timestamp;
    private final AuditEventType eventType;
    private final String details;
    private final String sourceHost;
    private final String sourceUser;

    public AuditEvent(AuditEventType eventType, String details) {
        this(System.currentTimeMillis(), eventType, details, getHostName(), getUserName());
    }

    public AuditEvent(long timestamp, AuditEventType eventType, String details,
                      String sourceHost, String sourceUser) {
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.details = details != null ? details : "";
        this.sourceHost = sourceHost != null ? sourceHost : "unknown";
        this.sourceUser = sourceUser != null ? sourceUser : "unknown";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public String getDetails() {
        return details;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public String getFormattedTimestamp() {
        return DISPLAY_FORMAT.format(Instant.ofEpochMilli(timestamp));
    }

    public byte[] toBytes() {
        byte[] typeBytes = eventType.name().getBytes(StandardCharsets.UTF_8);
        byte[] detailsBytes = details.getBytes(StandardCharsets.UTF_8);
        byte[] hostBytes = sourceHost.getBytes(StandardCharsets.UTF_8);
        byte[] userBytes = sourceUser.getBytes(StandardCharsets.UTF_8);

        int totalSize = 8 + (4 + typeBytes.length) + (4 + detailsBytes.length)
                + (4 + hostBytes.length) + (4 + userBytes.length);

        ByteBuffer buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(timestamp);

        buffer.putInt(typeBytes.length);
        buffer.put(typeBytes);

        buffer.putInt(detailsBytes.length);
        buffer.put(detailsBytes);

        buffer.putInt(hostBytes.length);
        buffer.put(hostBytes);

        buffer.putInt(userBytes.length);
        buffer.put(userBytes);

        return buffer.array();
    }

    public static AuditEvent fromBytes(byte[] data, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, data.length - offset).order(ByteOrder.BIG_ENDIAN);

        long timestamp = buffer.getLong();

        int typeLen = buffer.getInt();
        byte[] typeBytes = new byte[typeLen];
        buffer.get(typeBytes);
        AuditEventType eventType = AuditEventType.valueOf(new String(typeBytes, StandardCharsets.UTF_8));

        int detailsLen = buffer.getInt();
        byte[] detailsBytes = new byte[detailsLen];
        buffer.get(detailsBytes);
        String details = new String(detailsBytes, StandardCharsets.UTF_8);

        int hostLen = buffer.getInt();
        byte[] hostBytes = new byte[hostLen];
        buffer.get(hostBytes);
        String host = new String(hostBytes, StandardCharsets.UTF_8);

        int userLen = buffer.getInt();
        byte[] userBytes = new byte[userLen];
        buffer.get(userBytes);
        String user = new String(userBytes, StandardCharsets.UTF_8);

        return new AuditEvent(timestamp, eventType, details, host, user);
    }

    public int serializedSize() {
        byte[] typeBytes = eventType.name().getBytes(StandardCharsets.UTF_8);
        byte[] detailsBytes = details.getBytes(StandardCharsets.UTF_8);
        byte[] hostBytes = sourceHost.getBytes(StandardCharsets.UTF_8);
        byte[] userBytes = sourceUser.getBytes(StandardCharsets.UTF_8);
        return 8 + (4 + typeBytes.length) + (4 + detailsBytes.length)
                + (4 + hostBytes.length) + (4 + userBytes.length);
    }

    public String toCsvLine() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                getFormattedTimestamp(),
                eventType.name(),
                eventType.getSeverity().name(),
                escapeCsv(details),
                escapeCsv(sourceUser),
                escapeCsv(sourceHost));
    }

    public static String csvHeader() {
        return "\"Timestamp\",\"Event Type\",\"Severity\",\"Details\",\"User\",\"Host\"";
    }

    private String escapeCsv(String value) {
        return value.replace("\"", "\"\"");
    }

    private static String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return System.getenv("COMPUTERNAME") != null ? System.getenv("COMPUTERNAME") : "unknown";
        }
    }

    private static String getUserName() {
        return System.getProperty("user.name", "unknown");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (user=%s, host=%s)",
                getFormattedTimestamp(), eventType.getDisplayName(), details, sourceUser, sourceHost);
    }
}
