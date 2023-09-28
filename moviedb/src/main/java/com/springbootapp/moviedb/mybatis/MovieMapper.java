package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.Movie;
import com.springbootapp.moviedb.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MovieMapper {
    @Select("SELECT * FROM movies WHERE LOWER(name) LIKE '%' || LOWER(#{name}) || '%'")
    List<Movie> searchMovies(@Param("name") String name);

    @Insert("INSERT INTO like_movies (user_login, movie_id) VALUES (#{user.login}, #{movieId})")
    void insertLikeMovie(@Param("user") User user, @Param("movieId") int movieId);

    @Select("SELECT * FROM movies WHERE id = #{movieId}")
    List<Movie> getMovieById(@Param("movieId") int movieId);

    @Delete("DELETE FROM like_movies WHERE user_login = #{user.login} AND movie_id = #{movieId}")
    void deleteLikeMovie(@Param("user") User user, @Param("movieId") int movieId);

    @Select("SELECT like_movies.movie_id, movies.name, movies.year, movies.poster " +
            "FROM like_movies " +
            "INNER JOIN movies ON like_movies.movie_id = movies.id " +
            "WHERE like_movies.user_login = #{user.login}")
    List<Movie> getFavoriteMovies(@Param("user") User user);

    @Select("SELECT movies.id, movies.name, movies.year, movies.poster " +
            "FROM like_movies " +
            "INNER JOIN movies ON like_movies.movie_id = movies.id " +
            "WHERE like_movies.user_login = #{login}")
    List<Movie> getLikeAndDislikeMovies(@Param("login") String login);
}