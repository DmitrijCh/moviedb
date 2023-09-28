package com.springbootapp.moviedb.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.model.*;
import com.springbootapp.moviedb.mybatis.*;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("mybatis")
public class MyBatisStorage implements Storage {

    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;
    private final KeyMapper keyMapper;
    private final MovieInformMapper movieInformMapper;
    private final MovieMapper movieMapper;
    private final MovieRatingMapper movieRatingMapper;
    private final UserMapper userMapper;
    private final CommentMoviesMapper commentMoviesMapper;

    public MyBatisStorage(TokenUsers tokenUsers, Timestamp timestamp, KeyMapper keyMapper,
                          MovieInformMapper movieInformMapper, MovieMapper movieMapper,
                          MovieRatingMapper movieRatingMapper, UserMapper userMapper,
                          CommentMoviesMapper commentMoviesMapper) {
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
        this.keyMapper = keyMapper;
        this.movieInformMapper = movieInformMapper;
        this.movieMapper = movieMapper;
        this.movieRatingMapper = movieRatingMapper;
        this.userMapper = userMapper;
        this.commentMoviesMapper = commentMoviesMapper;
    }

    @Override
    public void addUsers(String name, String login, String password) {
        User user = new User(name, login, password);
        userMapper.addUser(user);
    }

    @Override
    public void addSessionKeys(String key, String login, String timestamp) {
        keyMapper.insertSessionKey(key, login, timestamp);
    }

    @Override
    public String registration(String login) {
        String key = tokenUsers.getKey();
        String stamp = timestamp.getStamp();
        addSessionKeys(key, login, stamp);
        Key keys = new Key();
        keys.setKey(key);
        keyMapper.insertKey(keys);
        try {
            return new ObjectMapper().writeValueAsString(keys);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(String key) {
        String userLogin = userMapper.getUserByKey(key);
        User user = new User();
        user.setLogin(userLogin);
        return user;
    }

    @Override
    public String searchKey(User user) {
        return userMapper.getNameByLogin(user.getLogin());
    }

    @Override
    public String verificationKey(String login, String password) {
        return userMapper.getKeyByLoginAndPassword(login, password);
    }

    @Override
    public List<Movie> getMovie(User user, int count, int offset, String type, String genres) {
        return movieInformMapper.getMoviesByUser(user.getLogin(), count, offset, type, genres);
    }

    @Override
    public List<Movie> searchMovies(String name) {
        return movieMapper.searchMovies(name);
    }

    @Override
    public List<Movie> getLikeMovie(User user, int movieId) {
        movieMapper.insertLikeMovie(user, movieId);
        return movieMapper.getMovieById(movieId);
    }

    @Override
    public List<Movie> deleteLikeMovie(User user, int movieId) {
        movieMapper.deleteLikeMovie(user, movieId);
        List<Movie> movies = new ArrayList<>();
        var movie = new Movie();
        movie.setId(movieId);
        movies.add(movie);
        return movies;
    }

    @Override
    public List<Movie> getFavoriteMovies(User user) {
        return movieMapper.getFavoriteMovies(user);
    }

    @Override
    public List<Movie> getLikeAndDislikeMovie(User user) {
        return movieMapper.getLikeAndDislikeMovies(user.getLogin());
    }

    @Override
    public int addRating(User user, int movieId, String rating) {
        int count = movieRatingMapper.getRatingCount(movieId, user.getLogin());

        if (count > 0) {
            movieRatingMapper.updateRating(movieId, user.getLogin(), rating);
        } else {
            movieRatingMapper.insertRating(movieId, user.getLogin(), rating);
        }
        return count;
    }

    @Override
    public List<MovieRating> getUserRating(User user) {
        return movieRatingMapper.getUserRating(user.getLogin());
    }

    @Override
    public List<MovieRating> getOverallRating() {
        return movieRatingMapper.getOverallRating();
    }

    @Override
    public void addCommentMovie(User user, int movieId, String comment) {
        commentMoviesMapper.addComment(user, movieId, comment);
    }

    @Override
    public List<CommentMovies> getOverallComments(int movieId) {
        return commentMoviesMapper.getAllComments(movieId);
    }

    @Override
    public String searchLogin(User user) {
        return userMapper.searchLoginByUser(user.getLogin());
    }
}