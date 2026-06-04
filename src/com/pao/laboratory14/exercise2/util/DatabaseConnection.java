package com.pao.laboratory14.exercise2.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton care gestioneaza conexiunea la baza de date.
 * Citeste configuratia din db.properties de pe classpath.
 *
 * Configurare IntelliJ: marcheaza 'exercise2/resources/' ca Resources Root.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws IOException, SQLException {
        Properties props = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
        if (is == null) {
            // fallback: resource might be under package resources directory
            is = getClass().getClassLoader().getResourceAsStream("com/pao/laboratory14/exercise2/resources/db.properties");
        }
        if (is == null) {
            // fallback: try relative file path in workspace (when resources are not on classpath)
            String cwd = System.getProperty("user.dir");
            String[] candidates = new String[] {
                cwd + "/src/com/pao/laboratory14/exercise2/resources/db.properties",
                cwd + "/paoj-2026/src/com/pao/laboratory14/exercise2/resources/db.properties",
                "src/com/pao/laboratory14/exercise2/resources/db.properties"
            };
            java.io.File f = null;
            for (String c : candidates) {
                java.io.File cf = new java.io.File(c);
                if (cf.exists()) { f = cf; break; }
            }
            if (f != null) {
                try (InputStream fis = new java.io.FileInputStream(f)) {
                    props.load(fis);
                }
            } else {
                throw new IOException(
                    "db.properties nu a fost gasit pe classpath. Marcheaza 'exercise2/resources/' ca Resources Root in IntelliJ: clic dreapta -> Mark Directory as -> Resources Root"
                );
            }
        } else {
            try (InputStream ris = is) {
                props.load(ris);
            }
        }
        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user", "");
        String password = props.getProperty("db.password", "");
        // Try to explicitly load SQLite driver to ensure registration with DriverManager
        try {
            Class.forName("org.sqlite.JDBC");
            try {
                // Try to instantiate and register driver explicitly to avoid classloader issues
                java.sql.Driver d = (java.sql.Driver) Class.forName("org.sqlite.JDBC").getDeclaredConstructor().newInstance();
                try {
                    java.sql.DriverManager.registerDriver(d);
                } catch (SQLException ignored) {
                }
            } catch (ReflectiveOperationException ignored) {
            }
        } catch (ClassNotFoundException ignored) {
        }
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public static DatabaseConnection getInstance() throws IOException, SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

