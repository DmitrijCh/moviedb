package com.springbootapp.moviedb.storage;

import com.springbootapp.moviedb.connection.ConnectionManager;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class Storage {


    public void addUsers(String name, String login, String password) throws SQLException {

        String query = "INSERT INTO users (name, login, password) VALUES (?,?,?)";

        PreparedStatement statement = ConnectionManager.connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, login);
        statement.setString(3, password);
        statement.execute();
    }

    public void addSessionKeys(String key, String login, String timestamp) throws SQLException {

        String query = "INSERT INTO session_keys (key, user_login, timestamp) VALUES (?, ?, ?)";

        PreparedStatement statement = ConnectionManager.connection.prepareStatement(query);
        statement.setString(1, key);
        statement.setString(2, login);
        statement.setString(3, timestamp);
        statement.execute();
    }
}
