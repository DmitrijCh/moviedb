package com.springbootapp.moviedb.model;

import javax.persistence.*;

@Entity
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserLogin(String login) {
        this.user = new User();
        this.user.setLogin(login);
    }
}









