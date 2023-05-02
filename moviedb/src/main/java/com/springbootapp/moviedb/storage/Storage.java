package com.springbootapp.moviedb.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.entity.Key;
import com.springbootapp.moviedb.entity.Movie;
import com.springbootapp.moviedb.entity.UserMessage;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Storage {

    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;
    private final Connection connection;

    @Autowired
    public Storage(TokenUsers tokenUsers, Timestamp timestamp, Connection connection) {
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
        this.connection = connection;
    }

    public void addUsers(String name, String login, String password) throws SQLException {

        String query = "INSERT INTO users (name, login, password) VALUES (?,?,?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, login);
        statement.setString(3, password);
        statement.execute();
    }

    public void addSessionKeys(String key, String login, String timestamp) throws SQLException {

        String query = "INSERT INTO session_keys (key, user_login, timestamp) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);
        statement.setString(2, login);
        statement.setString(3, timestamp);
        statement.execute();
    }

    public String registration(String login) throws SQLException, JsonProcessingException {

        String key = tokenUsers.getKey();
        String stamp = timestamp.getStamp();

        addSessionKeys(key, login, stamp);
        Key keys = new Key();
        keys.setKey(key);
        return new ObjectMapper().writeValueAsString(keys);
    }

    public String searchKey(String key) throws SQLException, JsonProcessingException {

        String query = "SELECT users.name FROM session_keys INNER JOIN users ON session_keys.user_login = users.login" +
                " WHERE key = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String keys = resultSet.getString(1);
            UserMessage user = new UserMessage();
            user.setMessage("Добро пожаловать, " + keys);
            return new ObjectMapper().writeValueAsString(user);
        } else {
            return new ObjectMapper().writeValueAsString("Error");
        }
    }

    public String verificationKey(String login, String password) throws SQLException, JsonProcessingException {

        String query = "SELECT session_keys.key FROM users INNER JOIN session_keys ON users.login = session_keys.user_login " +
                "WHERE login = ? AND password = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, login);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String key = resultSet.getString(1);
            Key keys = new Key();
            keys.setKey(key);
            return new ObjectMapper().writeValueAsString(keys);
        } else {
            return new ObjectMapper().writeValueAsString("Error");
        }
    }

    public List<Movie> getMovie(String key, int count, int offset) throws SQLException {
        List<Movie> result = new ArrayList<>();
        String query = "SELECT id, name, year, poster FROM movies WHERE EXISTS (SELECT 1 FROM session_keys WHERE key = ?) ORDER BY id LIMIT ? OFFSET ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);
        statement.setInt(2, count);
        statement.setInt(3, offset);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Movie movie = new Movie();
            movie.setId(Integer.valueOf(resultSet.getString("id")));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            result.add(movie);
        }
        return result;
    }

    public List<Movie> searchMovies(String name) throws SQLException {

        String query = "SELECT * FROM movies WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'\n";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();

        List<Movie> movies = new ArrayList<>();
        while (resultSet.next()) {
            Movie movie = new Movie();
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            movies.add(movie);
        }
        return movies;
    }

//    public List<Movie> getLikeMovie(String key, String name, String year, String poster) throws SQLException {
//
//        String query = "INSERT INTO like_movies (user_login, name, year, poster) " +
//                "SELECT user_login, ?, ?, ? FROM session_keys WHERE key = ?";
//
//        PreparedStatement statement = connection.prepareStatement(query);
//        statement.setString(1, name);
//        statement.setString(2, year);
//        statement.setString(3, poster);
//        statement.setString(4, key);
//        statement.executeUpdate();
//
//        List<Movie> movies = new ArrayList<>();
//        Movie movie = new Movie();
//        movie.setName(name);
//        movie.setYear(year);
//        movie.setPoster(poster);
//        movies.add(movie);
//        return movies;
//    }

    public List<Movie> getLikeMovie(String key, int movieId) throws SQLException {

        String insertQuery = "INSERT INTO like_movies (user_login, movie_id) " +
                "SELECT user_login, ? FROM session_keys WHERE key = ?";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setInt(1, movieId);
        insertStatement.setString(2, key);
        insertStatement.executeUpdate();

        List<Movie> movies = new ArrayList<>();

        String retrieveQuery = "SELECT * FROM movies WHERE id = ?";

        PreparedStatement retrieveStatement = connection.prepareStatement(retrieveQuery);
        retrieveStatement.setInt(1, movieId);

        ResultSet resultSet = retrieveStatement.executeQuery();

        if (resultSet.next()) {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            // Add the retrieved movie object to the 'movies' list
            movies.add(movie);
        }
        return movies;
    }

//    public List<Movie> deleteLikeMovie(String key, String name, String year, String poster) throws SQLException {
//
//        String query = "DELETE FROM like_movies WHERE user_login = (SELECT user_login FROM session_keys WHERE key = ?) AND name = ? AND year = ? AND poster = ?";
//
//        PreparedStatement statement = connection.prepareStatement(query);
//        statement.setString(1, key);
//        statement.setString(2, name);
//        statement.setString(3, year);
//        statement.setString(4, poster);
//        statement.executeUpdate();
//
//            List<Movie> movies = new ArrayList<>();
//            Movie movie = new Movie();
//            movie.setName(name);
//            movie.setYear(year);
//            movie.setPoster(poster);
//            movies.add(movie);
//            return movies;
//        }

    public List<Movie> deleteLikeMovie(String key, int movieID) throws SQLException {

        String query = "DELETE FROM like_movies WHERE user_login = (SELECT user_login FROM session_keys WHERE key = ?) AND movie_id = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);
        statement.setInt(2, movieID);
        statement.executeUpdate();

        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setId(movieID);
        movies.add(movie);
        return movies;
    }

//    public List<Movie> getFavoriteMovies(String key) throws SQLException {
//        List<Movie> result = new ArrayList<>();
//
//        String query = "SELECT like_movies.name, like_movies.year, like_movies.poster \n" +
//                "FROM like_movies \n" +
//                "INNER JOIN session_keys ON session_keys.user_login = like_movies.user_login \n" +
//                "WHERE session_keys.key = ?";
//
//        PreparedStatement statement = connection.prepareStatement(query);
//        statement.setString(1, key);
//
//        ResultSet resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//            Movie movie = new Movie();
//            movie.setName(resultSet.getString("name"));
//            movie.setYear(resultSet.getString("year"));
//            movie.setPoster(resultSet.getString("poster"));
//            result.add(movie);
//        }
//        return result;
//    }

    public List<Movie> getFavoriteMovies(String key) throws SQLException {
        List<Movie> result = new ArrayList<>();

        String query = "SELECT like_movies.movie_id, movies.name, movies.year, movies.poster " +
                "FROM like_movies " +
                "INNER JOIN session_keys ON session_keys.user_login = like_movies.user_login " +
                "INNER JOIN movies ON like_movies.movie_id = movies.id " +
                "WHERE session_keys.key = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, key);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("movie_id"));
            movie.setName(resultSet.getString("name"));
            movie.setYear(resultSet.getString("year"));
            movie.setPoster(resultSet.getString("poster"));
            result.add(movie);
        }
        return result;
    }
}


