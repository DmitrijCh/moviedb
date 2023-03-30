package com.springbootapp.moviedb.token;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Timestamp {
    public String getStamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date());
    }
}