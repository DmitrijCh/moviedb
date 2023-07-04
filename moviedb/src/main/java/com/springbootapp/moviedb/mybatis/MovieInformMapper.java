package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MovieInformMapper {
    @Select("SELECT id, name, year, poster, time, description, slogan, age_restriction, budget, country_ru, type " +
            "FROM movies " +
            "WHERE EXISTS (SELECT 1 FROM session_keys WHERE user_login = #{login}) " +
            "AND (#{type} = 'all' OR type = #{type}) " +
            "ORDER BY id LIMIT #{count} OFFSET #{offset}")
    List<Movie> getMoviesByUser(@Param("login") String login, @Param("count") int count, @Param("offset") int offset, @Param("type") String type);
}

