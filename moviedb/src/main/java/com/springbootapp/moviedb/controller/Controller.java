package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.connection.ConnectionManager;
import com.springbootapp.moviedb.entity.Key;
import com.springbootapp.moviedb.entity.User;
import com.springbootapp.moviedb.storage.TaskStorage;
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

    private final TaskStorage taskStorage;
    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;

    public Controller(TaskStorage taskStorage, TokenUsers tokenUsers, Timestamp timestamp) {
        this.taskStorage = taskStorage;
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
        System.out.println();
        System.out.println(name + " " + login + " " + password);
        System.out.println();
        taskStorage.addTask(name, login, password);

        String key = tokenUsers.key();
        String stamp = timestamp.stamp();
        taskStorage.addTaskSession(key, login, stamp);

        ObjectMapper mapper = new ObjectMapper();
        Key keys = new Key();
        keys.setKey(key);
        try {
            String jsonString = mapper.writeValueAsString(keys);
            System.out.println();
            System.out.println(jsonString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return mapper.writeValueAsString(keys);
    }

    @PostMapping("/message")
    public String messageUser(@RequestParam String key) {
        String saveRequest = "";
        String query = " SELECT users.name FROM session_keys INNER JOIN users ON session_keys.user_login = users.login" +
                " WHERE key = ?";
//        ' or true --
        try {
            PreparedStatement statement = ConnectionManager.connection.prepareStatement(query);
            statement.setString(1, key);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                query = resultSet.getString(1);
                System.out.println();
                saveRequest = query;
                System.out.println(saveRequest);
                System.out.println();
            }
            if (Objects.equals(saveRequest, query)) {
                ObjectMapper mapperTrue = new ObjectMapper();
                User user = new User();
                user.setMessage("Hello, " + saveRequest);
                try {
                    String jsonString = mapperTrue.writeValueAsString(user);
                    System.out.println(jsonString);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return mapperTrue.writeValueAsString(user);
            } else {
                ObjectMapper mapperFalse = new ObjectMapper();
                User user = new User();
                user.setMessage("Error");
                try {
                    String jsonString = mapperFalse.writeValueAsString(user);
                    System.out.println(jsonString);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return mapperFalse.writeValueAsString(user);
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}




