package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.connection.ConnectionManager;
import com.springbootapp.moviedb.entity.Key;
import com.springbootapp.moviedb.entity.UserMessage;
import com.springbootapp.moviedb.storage.Storage;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@RestController
public class Controller {

    private final Storage storage;
    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;

    public Controller(Storage storage, TokenUsers tokenUsers, Timestamp timestamp) {
        this.storage = storage;
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
    }

    @RequestMapping("/authorization")
    ModelAndView authorization() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form.html");
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String login, @RequestParam String password) throws JsonProcessingException, SQLException {
        storage.addUsers(name, login, password);

        String key = tokenUsers.key();
        String stamp = timestamp.stamp();

        storage.addSessionKeys(key, login, stamp);

        ObjectMapper mapper = new ObjectMapper();
        Key keys = new Key();
        keys.setKey(key);
        return mapper.writeValueAsString(keys);
    }

    @PostMapping("/message")
    public String messageUser(@RequestParam String key) throws SQLException, JsonProcessingException {
        String saveKey = " ";
        String query = "SELECT users.name FROM session_keys INNER JOIN users ON session_keys.user_login = users.login" +
                " WHERE key = ?";

        PreparedStatement statement = ConnectionManager.connection.prepareStatement(query);
        statement.setString(1, key);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            query = resultSet.getString(1);
            saveKey = query;
        }
        if (Objects.equals(saveKey, query)) {
            ObjectMapper mapperTrue = new ObjectMapper();
            UserMessage user = new UserMessage();
            user.setMessage("Добро пожаловать, " + saveKey);
            return mapperTrue.writeValueAsString(user);
        } else {
            return "Error";
        }
    }

    @PostMapping("/logins")
    public String loginUser(@RequestParam String login, @RequestParam String password) throws JsonProcessingException, SQLException {
        String saveKey = " ";
        String query = "SELECT session_keys.key FROM users INNER JOIN session_keys ON users.login = session_keys.user_login " +
                "WHERE login = ? AND password = ?";

        PreparedStatement statement = ConnectionManager.connection.prepareStatement(query);
        statement.setString(1, login);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            query = resultSet.getString(1);
            saveKey = query;
        }
        if (Objects.equals(saveKey, query)) {
            ObjectMapper mapper = new ObjectMapper();
            Key keys = new Key();
            keys.setKey(saveKey);
            return mapper.writeValueAsString(keys);
        } else {
            return "Error";
        }
    }
}

//localStorage.removeItem('key')
