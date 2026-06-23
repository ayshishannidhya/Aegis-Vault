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

import com.aegisvault.service.VaultService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AuditLog {

    private static final String AUDIT_LOG_PATH = "/__audit__/log";
    private static final String AUDIT_DIR_PATH = "/__audit__";
    private static final int DEFAULT_MAX_EVENTS = 10000;

    private final VaultService vaultService;
    private final List<AuditEvent> events;
    private final int maxEvents;
    private boolean initialized;

    public AuditLog(VaultService vaultService) {
        this(vaultService, DEFAULT_MAX_EVENTS);
    }

    public AuditLog(VaultService vaultService, int maxEvents) {
        this.vaultService = vaultService;
        this.maxEvents = maxEvents;
        this.events = new CopyOnWriteArrayList<>();
        this.initialized = false;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        try {
            if (!vaultService.exists(AUDIT_DIR_PATH)) {
                vaultService.createDirectory(AUDIT_DIR_PATH);
            }

            if (vaultService.exists(AUDIT_LOG_PATH)) {
                byte[] data = vaultService.readFile(AUDIT_LOG_PATH);
                if (data != null && data.length > 0) {
                    deserializeEvents(data);
                }
            }
        } catch (Exception e) {
            events.clear();
        }

        initialized = true;
    }

    public synchronized void record(AuditEventType eventType, String details) {
        if (!initialized) {
            initialize();
        }

        AuditEvent event = new AuditEvent(eventType, details);
        events.add(event);

        while (events.size() > maxEvents) {
            events.remove(0);
        }

        persist();
    }

    public List<AuditEvent> getEvents(int count) {
        if (!initialized) {
            initialize();
        }

        int start = Math.max(0, events.size() - count);
        return Collections.unmodifiableList(new ArrayList<>(events.subList(start, events.size())));
    }

    public List<AuditEvent> getAllEvents() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public List<AuditEvent> getEvents(long fromTimestamp, long toTimestamp) {
        if (!initialized) {
            initialize();
        }

        return events.stream()
                .filter(e -> e.getTimestamp() >= fromTimestamp && e.getTimestamp() <= toTimestamp)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuditEvent> getEventsByType(AuditEventType type) {
        if (!initialized) {
            initialize();
        }

        return events.stream()
                .filter(e -> e.getEventType() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuditEvent> getEventsBySeverity(AuditEventType.Severity severity) {
        if (!initialized) {
            initialize();
        }

        return events.stream()
                .filter(e -> e.getEventType().getSeverity() == severity)
                .collect(Collectors.toUnmodifiableList());
    }

    public int getEventCount() {
        if (!initialized) {
            initialize();
        }
        return events.size();
    }

    public void exportToCsv(Path outputPath) throws IOException {
        if (!initialized) {
            initialize();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(AuditEvent.csvHeader()).append("\n");

        for (AuditEvent event : events) {
            sb.append(event.toCsvLine()).append("\n");
        }

        Files.writeString(outputPath, sb.toString(), StandardCharsets.UTF_8);
    }

    private void persist() {
        try {
            byte[] data = serializeEvents();
            if (vaultService.exists(AUDIT_LOG_PATH)) {
                vaultService.writeFile(AUDIT_LOG_PATH, data);
            } else {
                vaultService.createFile(AUDIT_LOG_PATH, data);
            }
        } catch (Exception ignored) {
        }
    }

    private byte[] serializeEvents() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer countBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        countBuffer.putInt(events.size());

        try {
            baos.write(countBuffer.array());
            for (AuditEvent event : events) {
                byte[] eventBytes = event.toBytes();
                ByteBuffer lenBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
                lenBuffer.putInt(eventBytes.length);
                baos.write(lenBuffer.array());
                baos.write(eventBytes);
            }
        } catch (IOException e) {
            return new byte[0];
        }

        return baos.toByteArray();
    }

    private void deserializeEvents(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
            int count = buffer.getInt();

            for (int i = 0; i < count; i++) {
                int eventLen = buffer.getInt();
                byte[] eventBytes = new byte[eventLen];
                buffer.get(eventBytes);
                AuditEvent event = AuditEvent.fromBytes(eventBytes, 0);
                events.add(event);
            }
        } catch (Exception e) {
            events.clear();
        }
    }
}
