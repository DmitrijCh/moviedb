package com.springbootapp.moviedb.service;

import com.springbootapp.moviedb.entity.User;

public interface UserService {
    String login(User user);
    String registration(User user);
}
