package com.pao.BankingApp.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class AuditService {
    private static final AuditService INSTANCE = new AuditService();
    private final Path auditFile = Path.of("audit.csv");
    private final ReentrantLock lock = new ReentrantLock();

    private AuditService() {
        
    }

    public static AuditService getInstance() {
        return INSTANCE;
    }

    public void log(String action) {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String line = action + "," + timestamp + System.lineSeparator();
        lock.lock();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(auditFile.toFile(), true))) {
            w.write(line);
            w.flush();
        } catch (IOException e) {
        
            System.err.println("Failed to write audit: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
