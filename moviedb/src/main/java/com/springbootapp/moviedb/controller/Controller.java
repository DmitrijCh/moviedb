package com.springbootapp.moviedb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.message.JedisMessage;
import com.springbootapp.moviedb.message.KafkaMessage;
import com.springbootapp.moviedb.model.*;
import com.springbootapp.moviedb.storage.Storage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value =
            "Форма авторизации",
            notes = "Отображает форму авторизации для пользователей."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный запрос. Форма авторизации отображена."),
            @ApiResponse(code = 201, message = "Создано."),
            @ApiResponse(code = 204, message = "Успешно, но нет содержимого."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Введенная схема транзакции недопустима. Пожалуйста, проверьте правильность отправленных данных."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Ошибка была зарегистрирована и находится в процессе расследования. Пожалуйста, обратитесь к администратору системы.")
    })
    ModelAndView authorization() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form.html");
        return modelAndView;
    }

    @PostMapping("/register")
    @ApiOperation(value =
            "Регистрация нового пользователя",
            notes = "Регистрирует нового пользователя с указанным именем, логином и паролем."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Пользователь зарегистрирован."),
            @ApiResponse(code = 201, message = "Создано. Пользователь успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Введенные учетные данные недопустимы."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public String registerUser(@RequestParam String name, @RequestParam String login, @RequestParam String password) throws JsonProcessingException {
        storage.addUsers(name, login, password);
        return storage.registration(login);
    }

    @PostMapping("/logins")
    @ApiOperation(value =
            "Вход пользователя",
            notes = "Выполняет вход пользователя с указанным логином и паролем."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Пользователь вошел в систему."),
            @ApiResponse(code = 201, message = "Создано. Пользователь успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Введенные учетные данные недопустимы."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
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
    @ApiOperation(value =
            "Отправить сообщение пользователю",
            notes = "Отправляет сообщение пользователю с использованием ключа доступа."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Сообщение отправлено."),
            @ApiResponse(code = 201, message = "Создано. Сообщение успешно создано."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
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
    @ApiOperation(value =
            "Получить список фильмов",
            notes = "Получает список фильмов с учетом параметров запроса."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Список фильмов получен."),
            @ApiResponse(code = 201, message = "Создано. Список фильмов успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> messageMovie(@RequestParam String key, @RequestParam int count, @RequestParam int offset,
                                    @RequestParam String type, String genres) {
        User user = storage.getUser(key);
        return storage.getMovie(user, count, offset, type, genres);
    }

    @PostMapping("/search")
    @ApiOperation(value =
            "Поиск фильмов",
            notes = "Выполняет поиск фильмов по названию."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Фильмы найдены."),
            @ApiResponse(code = 201, message = "Создано. Фильмы успешно созданы."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> searchMovies(@RequestParam String name) {
        return storage.searchMovies(name);
    }

    @PostMapping("/message/movie/like")
    @ApiOperation(value =
            "Добавить фильм в избранное",
            notes = "Добавляет фильм в список избранных и отправляет сообщение."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Фильм добавлен в избранное и сообщение отправлено."),
            @ApiResponse(code = 201, message = "Создано. Фильм успешно создан и сообщение отправлено."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> likeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);

        String topic = "Test";
        String message = "Пользователь с логином  " + user.getLogin() + " добавил в избранное фильм с id: " + movieID;
        kafkaMessage.sendMessage(topic, message);

        return storage.getLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/dislike")
    @ApiOperation(value =
            "Удалить фильм из избранного",
            notes = "Удаляет фильм из списка избранных."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Фильм удален из избранного."),
            @ApiResponse(code = 201, message = "Создано. Фильм успешно удален из избранного."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> dislikeMovies(@RequestParam String key, @RequestParam int movieID) {
        User user = storage.getUser(key);
        return storage.deleteLikeMovie(user, movieID);
    }

    @PostMapping("/message/movie/favorites")
    @ApiOperation(value =
            "Получить список избранных фильмов",
            notes = "Получает список фильмов, добавленных в избранное."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Список избранных фильмов получен."),
            @ApiResponse(code = 201, message = "Создано. Список избранных фильмов успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> favoriteMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getFavoriteMovies(user);
    }

    @PostMapping("/message/movie/inform")
    @ApiOperation(value =
            "Получить список фильмов, добавленных и удаленных из избранного",
            notes = "Получает список фильмов, которые были добавлены и удалены из избранного."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Список фильмов получен."),
            @ApiResponse(code = 201, message = "Создано. Список фильмов успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<Movie> likeAndDislikeMovies(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getLikeAndDislikeMovie(user);
    }

    @PostMapping("/message/movie/rating")
    @ApiOperation(value =
            "Установить рейтинг фильму",
            notes = "Устанавливает рейтинг фильму на основе предоставленного ключа, идентификатора фильма и рейтинга."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Рейтинг фильма установлен."),
            @ApiResponse(code = 201, message = "Создано. Рейтинг фильма успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public int rating(@RequestParam String key, @RequestParam int movieID, @RequestParam String rating) {
        User user = storage.getUser(key);
        return storage.addRating(user, movieID, rating);
    }

    @PostMapping("/message/movie/user_rating")
    @ApiOperation(value =
            "Получить рейтинг пользователя",
            notes = "Получает рейтинг пользователя на основе предоставленного ключа."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Рейтинг пользователя получен."),
            @ApiResponse(code = 201, message = "Создано. Рейтинг пользователя успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<MovieRating> userRating(@RequestParam String key) {
        User user = storage.getUser(key);
        return storage.getUserRating(user);
    }

    @GetMapping("/message/movie/all_rating")
    @ApiOperation(value =
            "Получить общий рейтинг всех фильмов",
            notes = "Получает общий рейтинг всех фильмов на основе имеющейся информации."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Общий рейтинг всех фильмов получен."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<MovieRating> overallRating() {
        return storage.getOverallRating();
    }

    @PostMapping("/message/movie/comment")
    @ApiOperation(value =
            "Добавить комментарий к фильму",
            notes = "Добавляет комментарий к фильму на основе предоставленного ключа, идентификатора фильма и комментария."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Комментарий к фильму добавлен."),
            @ApiResponse(code = 201, message = "Создано. Комментарий к фильму успешно создан."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public String commentMovies(@RequestParam String key, @RequestParam int movieID, @RequestParam String comment) {
        User user = storage.getUser(key);
        storage.addCommentMovie(user, movieID, comment);
        return "OK";
    }

    @PostMapping("/message/movie/all_comments")
    @ApiOperation(value =
            "Получить все комментарии к фильму",
            notes = "Получает все комментарии к фильму на основе идентификатора фильма."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Все комментарии к фильму получены."),
            @ApiResponse(code = 201, message = "Создано."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
    public List<CommentMovies> overallComments(@RequestParam int movieID) {
        return storage.getOverallComments(movieID);
    }

    @PostMapping("/ping")
    @ApiOperation(value =
            "Проверить активность пользователя",
            notes = "Проверяет активность пользователя на основе предоставленного ключа доступа."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно. Активность пользователя проверена."),
            @ApiResponse(code = 201, message = "Создано."),
            @ApiResponse(code = 401, message = "Ошибка авторизации. Пожалуйста, удостоверьтесь в правильности вашего ключа доступа."),
            @ApiResponse(code = 403, message = "Доступ запрещен. Пожалуйста, проверьте правильность запроса."),
            @ApiResponse(code = 404, message = "Ресурс не найден. Произошла неожиданная ошибка. Обратитесь к администратору системы.")
    })
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