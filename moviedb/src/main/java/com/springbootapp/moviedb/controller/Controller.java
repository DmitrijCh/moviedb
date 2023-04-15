package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springbootapp.moviedb.entity.Movie;
import com.springbootapp.moviedb.storage.Storage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@RestController
public class Controller {

    private final Storage storage;

    public Controller(Storage storage) {
        this.storage = storage;
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
        return storage.registration(login);
    }

    @PostMapping("/logins")
    public String loginUser(@RequestParam String login, @RequestParam String password) throws JsonProcessingException, SQLException {
        return storage.verificationKey(login, password);
    }

    @PostMapping("/message/user")
    public String messageUser(@RequestParam String key) throws SQLException, JsonProcessingException {
        return storage.searchKey(key);
    }

    @PostMapping("/message/movie")
    public List<Movie> messageMovie(@RequestParam String key, @RequestParam int count, @RequestParam int offset) throws SQLException {
        return storage.getMovie(key, count, offset);
    }

    @PostMapping("/search")
    public List<Movie> searchMovies(@RequestParam String name) throws SQLException {
        return storage.searchMovies(name);
    }

    @PostMapping("/message/movie/like")
    public List<Movie> likeMovies(@RequestParam String key, @RequestParam String name, @RequestParam String year, @RequestParam String poster) throws SQLException {
        return storage.getLikeMovie(key, name, year, poster);
    }
}




