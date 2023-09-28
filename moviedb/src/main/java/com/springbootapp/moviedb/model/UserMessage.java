package com.springbootapp.moviedb.model;

import lombok.Getter;

@Getter
public class UserMessage {
    private String message;

    public UserMessage() {
    }

    public void setMessage(String message) {
        this.message = message;
    }
}