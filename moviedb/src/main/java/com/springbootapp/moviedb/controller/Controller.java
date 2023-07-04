package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.model.*;
import com.springbootapp.moviedb.storage.Storage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class Controller {

    private final Storage storage;

    @Autowired
    public Controller(@Qualifier("jdbc") Storage storage) {
        this.storage = storage;
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
    public List<Movie> messageMovie(@RequestParam String key, @RequestParam int count, @RequestParam int offset, @RequestParam String type) {
        User user = storage.getUser(key);
        return storage.getMovie(user, count, offset, type);
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

    @PostMapping("/message/movie/comment")
    public String commentMovies(@RequestParam String key, @RequestParam int movieID, @RequestParam String comment) {
        User user = storage.getUser(key);
        storage.addCommentMovie(user, movieID, comment);
        return "Ok";
    }

    @PostMapping("/message/movie/all_comments")
    public List<CommentMovies> overallComments(@RequestParam int movieID) {
        return storage.getOverallComments(movieID);
    }

    @PostMapping("/ping")
    public String ping(@RequestParam String key) {
        User user = storage.getUser(key);
        String name = storage.searchLogin(user);

        try (JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost")) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(name, "active");
                jedis.expire(name, 10L);

                Set<String> expiredKeys = jedis.keys("*").stream()
                        .filter(k -> jedis.ttl(k) < 0)
                        .collect(Collectors.toSet());
                if (!expiredKeys.isEmpty()) {
                    jedis.del(expiredKeys.toArray(new String[0]));
                }

                Long keyCount = (long) jedis.keys("*").size();

                System.out.println("Уникальных ключей получено: " + keyCount);
                System.out.println("Пользователь с логином" + " " + name + " " + "онлайн");

                return String.valueOf(keyCount);
            }
        }
    }
}

