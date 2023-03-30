package com.springbootapp.moviedb.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.entity.Key;
import com.springbootapp.moviedb.entity.Movie;
import com.springbootapp.moviedb.entity.UserMessage;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Storage {

    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;
    private final Connection connection;

    @Autowired
    public Storage(TokenUsers tokenUsers, Timestamp timestamp, Connection connection) {
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
        this.connection = connection;
    }
    public void addUsers(String name, String login, String password) throws SQLException {

        String query = "INSERT INTO users (name, login, password) VALUES (?,?,?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, login);
        statement.setString(3, password);
        statement.execute();
    }

    public void addSessionKeys(String key, String login, String timestamp) throws SQLException {

        String query = "INSERT INTO session_keys (key, user_login, timestamp) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);
        statement.setString(2, login);
        statement.setString(3, timestamp);
        statement.execute();
    }

    public String registration(String login) throws SQLException, JsonProcessingException {

        String key = tokenUsers.getKey();
        String stamp = timestamp.getStamp();

        addSessionKeys(key, login, stamp);
        Key keys = new Key();
        keys.setKey(key);
        return new ObjectMapper().writeValueAsString(keys);
    }

    public String searchKey(String key) throws SQLException, JsonProcessingException {

        String query = "SELECT users.name FROM session_keys INNER JOIN users ON session_keys.user_login = users.login" +
                " WHERE key = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String keys = resultSet.getString(1);
            UserMessage user = new UserMessage();
            user.setMessage("Добро пожаловать, " + keys);
            return new ObjectMapper().writeValueAsString(user);
        } else {
            return new ObjectMapper().writeValueAsString("Error");
        }
    }

    public List<Movie> getMovie() throws SQLException, JsonProcessingException {

        List<Movie> result = new ArrayList<>();

        String query = "SELECT * FROM movies";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {

            Movie movie = new Movie();
            movie.setName(resultSet.getString(1));
            movie.setYear(resultSet.getString(2));
            movie.setPoster(resultSet.getString(3));
            result.add(movie);
        }
        return result;
    }

    public String verificationKey(String login, String password) throws SQLException, JsonProcessingException {

        String query = "SELECT session_keys.key FROM users INNER JOIN session_keys ON users.login = session_keys.user_login " +
                "WHERE login = ? AND password = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, login);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String key = resultSet.getString(1);
            Key keys = new Key();
            keys.setKey(key);
            return new ObjectMapper().writeValueAsString(keys);
        } else {
            return new ObjectMapper().writeValueAsString("Error");
        }
    }
}
