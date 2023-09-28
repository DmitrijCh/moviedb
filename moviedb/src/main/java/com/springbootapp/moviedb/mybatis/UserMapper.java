package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT user_login FROM session_keys WHERE key = #{key} LIMIT 1")
    String getUserByKey(String key);

    @Insert("INSERT INTO users (name, login, password) VALUES (#{name}, #{login}, #{password})")
    void addUser(User user);

    @Select("SELECT name FROM users WHERE login = #{login}")
    String getNameByLogin(String login);

    @Select("SELECT session_keys.key FROM users INNER JOIN session_keys ON users.login = session_keys.user_login " +
            "WHERE users.login = #{login} AND users.password = #{password}")
    String getKeyByLoginAndPassword(@Param("login") String login, @Param("password") String password);

    @Select("SELECT login FROM users WHERE login = #{login}")
    String searchLoginByUser(String login);
}