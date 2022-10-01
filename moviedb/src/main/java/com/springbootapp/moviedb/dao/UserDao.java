package com.springbootapp.moviedb.dao;

import com.springbootapp.moviedb.entity.User;

public interface UserDao {
    User findByLogin(String login);
    Boolean save(User user);
}
