package com.pao.BankingApp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaRunner {

    public static void runSchemaIfRequested() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        String init = db.getProperty("db.init", "false");
        if (!"true".equalsIgnoreCase(init)) return;
        runSchema();
    }

    public static void runSchema() {
        try (InputStream in = SchemaRunner.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                System.err.println("schema.sql not found on classpath (expected in resources/schema.sql)");
                return;
            }

            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append('\n');
                }
            }

            executeSqlScript(sql.toString());
        } catch (IOException e) {
            System.err.println("Failed to read schema.sql: " + e.getMessage());
        }
    }

    private static void executeSqlScript(String script) {
        // Build statements by accumulating lines until a semicolon terminates a statement.
        StringBuilder current = new StringBuilder();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            for (String line : script.split("\n")) {
                String trimmed = line.trim();
                // Skip SQL comments
                if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.isEmpty()) {
                    continue;
                }
                current.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String toExecute = current.toString().trim();
                    // remove trailing semicolon
                    if (toExecute.endsWith(";")) toExecute = toExecute.substring(0, toExecute.length() - 1).trim();
                    if (!toExecute.isEmpty()) {
                        try {
                            stmt.execute(toExecute);
                        } catch (SQLException e) {
                            System.err.println("Failed to execute statement: " + e.getMessage());
                        }
                    }
                    current.setLength(0);
                }
            }
            // any leftover (no trailing semicolon) -> execute as well
            String leftover = current.toString().trim();
            if (!leftover.isEmpty()) {
                try {
                    stmt.execute(leftover);
                } catch (SQLException e) {
                    System.err.println("Failed to execute leftover statement: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to run schema: " + e.getMessage());
        }
    }
}
