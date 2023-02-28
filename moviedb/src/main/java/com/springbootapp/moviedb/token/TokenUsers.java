package com.springbootapp.moviedb.token;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUsers {
    public String key() {
        return UUID.randomUUID().toString();
    }
}