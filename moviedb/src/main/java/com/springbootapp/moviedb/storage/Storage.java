package com.springbootapp.moviedb.storage;

import com.springbootapp.moviedb.model.CommentMovies;
import com.springbootapp.moviedb.model.Movie;
import com.springbootapp.moviedb.model.MovieRating;
import com.springbootapp.moviedb.model.User;

import java.util.List;

public interface Storage {

    void addUsers(String name, String login, String password);

    void addSessionKeys(String key, String login, String timestamp);

    String registration(String login);

    User getUser(String key);

    String searchKey(User user);

    String verificationKey(String login, String password);

    List<Movie> getMovie(User user, int count, int offset, String type, String genres);

    List<Movie> searchMovies(String name);

    List<Movie> getLikeMovie(User user, int movieId);

    List<Movie> deleteLikeMovie(User user, int movieId);

    List<Movie> getFavoriteMovies(User user);

    List<Movie> getLikeAndDislikeMovie(User user);

    int addRating(User user, int movieId, String rating);

    List<MovieRating> getUserRating(User user);

    List<MovieRating> getOverallRating();

    void addCommentMovie(User user, int movieId, String comment);

    List<CommentMovies> getOverallComments(int movieId);

    String searchLogin(User user);
}
