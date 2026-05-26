package com.pao.BankingApp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private final Properties props = new Properties();

    private DatabaseConnection() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a new Connection. Callers must close it.
     */
    public Connection getConnection() throws SQLException {
    try {
        Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
        throw new SQLException("SQLite JDBC driver not found in classpath", e);
    }

    String url = props.getProperty("db.url", "jdbc:sqlite:./paoj_proiect.db");
    String user = props.getProperty("db.user", "");
    String password = props.getProperty("db.password", "");

    if (user.isEmpty() && password.isEmpty()) {
        return DriverManager.getConnection(url);
    }

    return DriverManager.getConnection(url, user, password);
}

    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
