package com.springbootapp.moviedb.token;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Timestamp {
    public String stamp() {
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date());
        System.out.println(timeStamp);
        return timeStamp;
    }
}