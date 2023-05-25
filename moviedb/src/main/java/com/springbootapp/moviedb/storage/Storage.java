package com.springbootapp.moviedb.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.entity.*;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Storage {

    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Storage(TokenUsers tokenUsers, Timestamp timestamp, JdbcTemplate jdbcTemplate) {
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addUsers(String name, String login, String password) {
        String query = "INSERT INTO users (name, login, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, name, login, password);
    }

    public void addSessionKeys(String key, String login, String timestamp) {
        String query = "INSERT INTO session_keys (key, user_login, timestamp) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, key, login, timestamp);
    }

    public String registration(String login) throws JsonProcessingException {
        String key = tokenUsers.getKey();
        String stamp = timestamp.getStamp();
        addSessionKeys(key, login, stamp);
        Key keys = new Key();
        keys.setKey(key);
        return new ObjectMapper().writeValueAsString(keys);
    }

    public User getUser(String key) {
        String query = "SELECT user_login FROM session_keys WHERE key = ?";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            String login = resultSet.getString("user_login");
            return new User(login);
        }, key);
    }

    public String searchKey(User user) {
        String query = "SELECT name FROM users WHERE login = ?";
        return jdbcTemplate.queryForObject(query, String.class, user.getLogin());
    }

    public String verificationKey(String login, String password) {
        String query = "SELECT session_keys.key FROM users INNER JOIN session_keys ON users.login = session_keys.user_login " +
                "WHERE login = ? AND password = ?";
        return jdbcTemplate.queryForObject(query, String.class, login, password);
    }

    public List<MovieInform> getMovie(User user, int count, int offset) {
        String query = "SELECT id, name, year, poster, time, description, slogan, age_restriction, budget, country_ru, type " +
                "FROM movies " +
                "WHERE EXISTS (SELECT 1 FROM session_keys WHERE user_login = ?) " +
                "ORDER BY id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            MovieInform movie = new MovieInform();
            movie.setId(resultSet.getInt("id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            movie.setTime(resultSet.getString("time"));
            movie.setDescription(resultSet.getString("description"));
            movie.setSlogan(resultSet.getString("slogan"));
            movie.setAge((resultSet.getString("age_restriction")));
            movie.setBudget(resultSet.getString("budget"));
            movie.setCountry(resultSet.getString("country_ru"));
            movie.setType(resultSet.getString("type"));
            return movie;
        }, user.getLogin(), count, offset);
    }

    public List<Movie> searchMovies(String name) {
        String query = "SELECT * FROM movies WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'";
        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Movie movie = new Movie();
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            return movie;
        }, name);
    }

    public List<Movie> getLikeMovie(User user, int movieId) {
        String insertQuery = "INSERT INTO like_movies (user_login, movie_id) VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, user.getLogin(), movieId);

        String retrieveQuery = "SELECT * FROM movies WHERE id = ?";
        return jdbcTemplate.query(retrieveQuery, (resultSet, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            return movie;
        }, movieId);
    }

    public List<Movie> deleteLikeMovie(User user, int movieID) {
        String query = "DELETE FROM like_movies WHERE user_login = ? AND movie_id = ?";
        jdbcTemplate.update(query, user.getLogin(), movieID);
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setId(movieID);
        movies.add(movie);
        return movies;
    }

    public List<Movie> getFavoriteMovies(User user) {
        String query = "SELECT like_movies.movie_id, movies.name, movies.year, movies.poster " +
                "FROM like_movies " +
                "INNER JOIN movies ON like_movies.movie_id = movies.id " +
                "WHERE like_movies.user_login = ?";
        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("movie_id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            return movie;
        }, user.getLogin());
    }

    public List<Movie> getLikeAndDislikeMovie(User user) {
        String query = "SELECT movies.id, movies.name, movies.year, movies.poster " +
                "FROM like_movies " +
                "INNER JOIN movies ON like_movies.movie_id = movies.id " +
                "WHERE like_movies.user_login = ?";
        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            return movie;
        }, user.getLogin());
    }

    public int addRating(User user, int movieId, String rating) {
        String query = "SELECT COUNT(*) FROM rating_movies WHERE movie_id = ? AND user_login = ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, movieId, user.getLogin());

        if (count > 0) {
            String updateQuery = "UPDATE rating_movies SET rating = ? WHERE movie_id = ? AND user_login = ?";
            jdbcTemplate.update(updateQuery, rating, movieId, user.getLogin());
        } else {
            String insertQuery = "INSERT INTO rating_movies (movie_id, user_login, rating) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertQuery, movieId, user.getLogin(), rating);
        }
        return count;
    }

    public List<MovieRating> getUserRating(User user) {
        String query = "SELECT movie_id, user_login, rating FROM rating_movies WHERE user_login = ?";

        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            MovieRating movieRating = new MovieRating();
            movieRating.setId(resultSet.getInt("movie_id"));
            movieRating.setName(resultSet.getString("user_login"));
            movieRating.setRating(resultSet.getString("rating"));
            return movieRating;
        }, user.getLogin());
    }

    public List<MovieRating> getOverallRating() {
        String query = "SELECT movie_id, AVG(CAST(rating AS numeric)) AS avg_rating " +
                "FROM rating_movies " +
                "WHERE movie_id IN (" +
                "    SELECT movie_id " +
                "    FROM rating_movies " +
                "    GROUP BY movie_id " +
                "    HAVING COUNT(DISTINCT user_login) > 1 " +
                ") " +
                "GROUP BY movie_id";

        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            MovieRating movieRating = new MovieRating();
            movieRating.setId(resultSet.getInt("movie_id"));
            double avgRating = resultSet.getDouble("avg_rating");
            movieRating.setRating(Double.toString(avgRating));
            return movieRating;
        });
    }
}


