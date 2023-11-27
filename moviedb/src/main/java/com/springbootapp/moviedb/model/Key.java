package com.springbootapp.moviedb.model;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "session_keys")
public class Key {

    @Id
    @Column(name = "key")
    private String key;

    @OneToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
    private User user;

    @Column(name = "timestamp")
    private String timestamp;

    public Key() {
    }

    public Key(String key, User user, String timestamp) {
        this.key = key;
        this.user = user;
        this.timestamp = timestamp;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserLogin(String login) {
        this.user = new User();
        this.user.setLogin(login);
    }
}