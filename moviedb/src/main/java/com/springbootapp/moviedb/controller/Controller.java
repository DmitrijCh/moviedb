package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.entity.*;
import com.springbootapp.moviedb.storage.Storage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class Controller {

    private final Storage storage;

    public Controller(Storage storage) {
        this.storage = storage;
        ;
    }

    @RequestMapping("/authorization")
    ModelAndView authorization() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form.html");
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String login, @RequestParam String password) throws JsonProcessingException {
        storage.addUsers(name, login, password);
        return storage.registration(login);
    }

    @PostMapping("/logins")
    public String loginUser(@RequestParam String login, @RequestParam String password) throws JsonProcessingException {
        String key = storage.verificationKey(login, password);

        if (key != null) {
            Key keys = new Key();
            keys.setKey(key);
            return new ObjectMapper().writeValueAsString(keys);
        } else {
            return "Error";
        }
    }

    @PostMapping("/message/user")
    public String messageUser(@RequestParam String key) throws JsonProcessingException {
        User user = storage.getUser(key);
        String name = storage.searchKey(user);

        if (name != null) {
            UserMessage userMessage = new UserMessage();
            userMessage.setMessage("Добро пожаловать, " + name);
            return new ObjectMapper().writeValueAsString(userMessage);
        } else {
            return "Error";
        }
    }

    @PostMapping("/message/movie")
    public List<MovieInform> messageMovie(@RequestParam String key, @RequestParam int count, @RequestParam int offset) {
        User user = storage.getUser(key);
        return storage.getMovie(user, count, offset);
    }

    @PostMapping("/search")
    public List<Movie> searchMovies(@RequestParam String name) {
        return storage.searchMovies(name);
    }

    @PostMapping("/message/movie/like")
    public List<Movie> likeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);
        return storage.getLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/dislike")
    public List<Movie> dislikeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);
        return storage.deleteLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/favorites")
    public List<Movie> favoriteMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getFavoriteMovies(user);
    }

    @PostMapping("/message/movie/inform")
    public List<Movie> likeAndDislikeMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getLikeAndDislikeMovie(user);
    }

    @PostMapping("/message/movie/rating")
    public int rating(@RequestParam String key, @RequestParam int movieID, @RequestParam String rating) {
        User user = storage.getUser(key);
        return storage.addRating(user, movieID, rating);
    }

    @PostMapping("/message/movie/user_rating")
    public List<MovieRating> userRating(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getUserRating(user);
    }

    @GetMapping("/message/movie/all_rating")
    public List<MovieRating> overallRating() {
        return storage.getOverallRating();
    }
}




