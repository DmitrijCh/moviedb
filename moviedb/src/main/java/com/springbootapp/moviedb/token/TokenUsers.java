package com.springbootapp.moviedb.token;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUsers {
    public String getKey() {
        return UUID.randomUUID().toString();
    }
}