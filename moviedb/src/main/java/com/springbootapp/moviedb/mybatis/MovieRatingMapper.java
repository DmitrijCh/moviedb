package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.MovieRating;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MovieRatingMapper {

    @Select("SELECT COUNT(*) FROM rating_movies WHERE movie_id = #{movieId} AND user_login = #{login}")
    int getRatingCount(@Param("movieId") int movieId, @Param("login") String login);

    @Update("UPDATE rating_movies SET rating = #{rating} WHERE movie_id = #{movieId} AND user_login = #{login}")
    void updateRating(@Param("movieId") int movieId, @Param("login") String login, @Param("rating") String rating);

    @Insert("INSERT INTO rating_movies (movie_id, user_login, rating) VALUES (#{movieId}, #{login}, #{rating})")
    void insertRating(@Param("movieId") int movieId, @Param("login") String login, @Param("rating") String rating);

    @Select("SELECT movie_id, user_login, rating FROM rating_movies WHERE user_login = #{login}")
    @Results({
            @Result(property = "id", column = "movie_id"),
            @Result(property = "name", column = "user_login")
    })
    List<MovieRating> getUserRating(@Param("login") String login);

    @Select("SELECT movie_id, AVG(CAST(rating AS numeric)) AS avg_rating " +
            "FROM rating_movies " +
            "GROUP BY movie_id " +
            "HAVING COUNT(DISTINCT user_login) > 1")
    @Results({
            @Result(property = "id", column = "movie_id"),
            @Result(property = "rating", column = "avg_rating")
    })
    List<MovieRating> getOverallRating();
}