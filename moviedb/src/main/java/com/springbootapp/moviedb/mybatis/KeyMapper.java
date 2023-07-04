package com.springbootapp.moviedb.mybatis;

import com.springbootapp.moviedb.model.Key;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KeyMapper {
    @Insert("INSERT INTO session_keys (key) VALUES (#{key})")
    void insertKey(Key key);

    @Insert("INSERT INTO session_keys (key, user_login, timestamp) VALUES (#{key}, #{login}, #{timestamp})")
    void insertSessionKey(@Param("key") String key, @Param("login") String login, @Param("timestamp") String timestamp);
}
