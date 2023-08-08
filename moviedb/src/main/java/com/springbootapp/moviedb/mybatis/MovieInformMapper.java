package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MovieInformMapper {
    @Select("SELECT id, name, year, poster, time, description, slogan, age_restriction, budget, country_ru, type, persons, genres " +
            "FROM movies " +
            "WHERE EXISTS (SELECT 1 FROM session_keys WHERE user_login = #{login}) " +
            "AND (#{type} = 'all' OR type = #{type}) " +
            "AND (" +
            "   #{genres} = 'all' OR " +
            "   (#{genres} = 'мультфильм' AND genres LIKE '%мультфильм%') OR " +
            "   (#{genres} = 'драма' AND genres LIKE '%драма%') OR " +
            "   (#{genres} = 'мелодрама' AND genres LIKE '%мелодрама%') OR " +
            "   (#{genres} = 'фэнтези' AND genres LIKE '%фэнтези%') OR " +
            "   (#{genres} = 'комедия' AND genres LIKE '%комедия%') OR " +
            "   (#{genres} = 'фантастика' AND genres LIKE '%фантастика%') OR " +
            "   (#{genres} = 'ужасы' AND genres LIKE '%ужасы%') OR " +
            "   (#{genres} = 'мюзикл' AND genres LIKE '%мюзикл%') OR " +
            "   (#{genres} = 'семейный' AND genres LIKE '%семейный%') OR " +
            "   (#{genres} = 'приключения' AND genres LIKE '%приключения%') OR " +
            "   (#{genres} = 'короткометражка' AND genres LIKE '%короткометражка%') OR " +
            "   (#{genres} = 'триллер' AND genres LIKE '%триллер%') OR " +
            "   (#{genres} = 'криминал' AND genres LIKE '%криминал%') OR " +
            "   (#{genres} = 'боевик' AND genres LIKE '%боевик%') OR " +
            "   (#{genres} = 'биография' AND genres LIKE '%биография%') OR " +
            "   (#{genres} = 'история' AND genres LIKE '%история%') OR " +
            "   (#{genres} = 'военный' AND genres LIKE '%военный%') OR " +
            "   (#{genres} = 'спорт' AND genres LIKE '%спорт%') OR " +
            "   (#{genres} = 'музыка' AND genres LIKE '%музыка%') OR " +
            "   (#{genres} = 'вестерн' AND genres LIKE '%вестерн%') OR " +
            "   (#{genres} = 'детектив' AND genres LIKE '%детектив%') OR " +
            "   (#{genres} = 'документальный' AND genres LIKE '%документальный%') OR " +
            "   (#{genres} = 'фильм-нуар' AND genres LIKE '%фильм-нуар%') OR " +
            "   (#{genres} = 'аниме' AND genres LIKE '%аниме%')" +
            ") " +
            "ORDER BY id LIMIT #{count} OFFSET #{offset}"
    )
    List<Movie> getMoviesByUser(@Param("login") String login, @Param("count") int count, @Param("offset") int offset, @Param("type") String type, @Param("genres") String genres);
}


