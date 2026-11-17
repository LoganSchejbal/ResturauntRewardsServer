package com.lschej.rewardsserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseController {

    private static final HikariDataSource source = new HikariDataSource(configDataSource());

    private DatabaseController() {
    }

    private static String getUrl() {
        return enviromentVariable("DB_URL");
    }

    private static String getUser() {
        return enviromentVariable("DB_USER");
    }

    private static String getPassword() {
        return enviromentVariable("DB_PASSWORD");
    }

    private static String enviromentVariable(String varName) {
        String variable = System.getenv(varName);

        if (variable == null || variable.isBlank()) {
            throw new IllegalStateException("Unconfigured Enviroment Variable: " + varName);
        }

        return variable;
    }

    public static HikariConfig configDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getUrl());
        config.setUsername(getUser());
        config.setPassword(getPassword());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("Rewards_Server");

        return config;
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());

        return DriverManager.getConnection(getUrl(), properties);

    }

    public static void closePool() {
        if (!source.isClosed()) {
            source.close();
        }
    }
}
