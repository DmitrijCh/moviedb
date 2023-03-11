package com.springbootapp.moviedb.connection;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class ConnectionManager {

    public static Connection connection;

    public void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "dima";
        String password = "193105610";
        connection = DriverManager.getConnection(url, user, password);
    }
}

