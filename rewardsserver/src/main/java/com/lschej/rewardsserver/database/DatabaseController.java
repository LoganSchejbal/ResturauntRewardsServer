package com.lschej.rewardsserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseController {

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

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());

        return DriverManager.getConnection(getUrl(), properties);

    }
}
