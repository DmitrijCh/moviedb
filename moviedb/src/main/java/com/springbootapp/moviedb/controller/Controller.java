package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.message.JedisMessage;
import com.springbootapp.moviedb.message.KafkaMessage;
import com.springbootapp.moviedb.model.*;
import com.springbootapp.moviedb.storage.Storage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Api(tags = "Controller")
@RestController
public class Controller {

    private final Storage storage;
    private final KafkaMessage kafkaMessage;
    private final JedisMessage jedisMessage;

    @Autowired
    public Controller(@Qualifier("jdbc") Storage storage, KafkaMessage kafkaMessage, JedisMessage jedisMessage) {
        this.storage = storage;
        this.kafkaMessage = kafkaMessage;
        this.jedisMessage = jedisMessage;
    }

    @RequestMapping("/authorization")
    @ApiOperation("Форма авторизации")
    ModelAndView authorization() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form.html");
        return modelAndView;
    }

    @PostMapping("/register")
    @ApiOperation("Регистрация нового пользователя")
    public String registerUser(@RequestParam String name, @RequestParam String login, @RequestParam String password) throws JsonProcessingException {
        storage.addUsers(name, login, password);
        return storage.registration(login);
    }

    @PostMapping("/logins")
    @ApiOperation("Вход пользователя")
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
    @ApiOperation("Отправить сообщение пользователю")
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
    @ApiOperation("Получить список фильмов")
    public List<Movie> messageMovie(@RequestParam String key, @RequestParam int count, @RequestParam int offset,
                                    @RequestParam String type, String genres) {
        User user = storage.getUser(key);
        return storage.getMovie(user, count, offset, type, genres);
    }

    @PostMapping("/search")
    @ApiOperation("Поиск фильмов")
    public List<Movie> searchMovies(@RequestParam String name) {
        return storage.searchMovies(name);
    }

    @PostMapping("/message/movie/like")
    @ApiOperation("Добавить фильм в избранное")
    public List<Movie> likeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);

        String topic = "Test";
        String message = "Пользователь с логином  " + user.getLogin() + " добавил в избранное фильм с id: " + movieID;
        kafkaMessage.sendMessage(topic, message);

        return storage.getLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/dislike")
    @ApiOperation("Удалить фильм из избранного")
    public List<Movie> dislikeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);
        return storage.deleteLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/favorites")
    @ApiOperation("Получить список избранных фильмов")
    public List<Movie> favoriteMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getFavoriteMovies(user);
    }

    @PostMapping("/message/movie/inform")
    @ApiOperation("Получить список фильмов, добавленных и удаленных из избранного")
    public List<Movie> likeAndDislikeMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getLikeAndDislikeMovie(user);
    }

    @PostMapping("/message/movie/rating")
    @ApiOperation("Установить рейтинг фильму")
    public int rating(@RequestParam String key, @RequestParam int movieID, @RequestParam String rating) {
        User user = storage.getUser(key);
        return storage.addRating(user, movieID, rating);
    }

    @PostMapping("/message/movie/user_rating")
    @ApiOperation("Получить рейтинг пользователя")
    public List<MovieRating> userRating(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getUserRating(user);
    }

    @GetMapping("/message/movie/all_rating")
    @ApiOperation("Получить общий рейтинг всех фильмов")
    public List<MovieRating> overallRating() {
        return storage.getOverallRating();
    }

    @PostMapping("/message/movie/comment")
    @ApiOperation("Добавить комментарий к фильму")
    public String commentMovies(@RequestParam String key, @RequestParam int movieID, @RequestParam String comment) {
        User user = storage.getUser(key);
        storage.addCommentMovie(user, movieID, comment);
        return "OK";
    }

    @PostMapping("/message/movie/all_comments")
    @ApiOperation("Получить все комментарии к фильму")
    public List<CommentMovies> overallComments(@RequestParam int movieID) {
        return storage.getOverallComments(movieID);
    }

    @PostMapping("/ping")
    @ApiOperation("Проверить активность пользователя")
    public String ping(@RequestParam String key) {
        User user = storage.getUser(key);
        String name = storage.searchLogin(user);

        jedisMessage.setKey(name, "active");
        jedisMessage.deleteExpiredKeys();

        Long keyCount = jedisMessage.getKeyCount();

        System.out.println("Уникальных ключей получено: " + keyCount);
        System.out.println("Пользователь с логином " + name + " онлайн");
        return String.valueOf(keyCount);
    }
}