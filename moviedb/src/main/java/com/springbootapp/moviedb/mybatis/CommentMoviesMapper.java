package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.CommentMovies;
import com.springbootapp.moviedb.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMoviesMapper {
    @Insert("INSERT INTO comment_movies (user_Login, movie_Id, comment) VALUES (#{user.login}, #{movieId}, #{comment})")
    void addComment(@Param("user") User user, @Param("movieId") int movieId, @Param("comment") String comment);

    @Results({
            @Result(property = "userLogin", column = "name"),
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "comment", column = "comment")
    })
    @Select("SELECT u.name, cm.movie_id, cm.comment " +
            "FROM comment_movies cm " +
            "JOIN users u ON cm.user_login = u.login " +
            "WHERE cm.movie_id = #{movieId}")
    List<CommentMovies> getAllComments(@Param("movieId") int movieId);
}