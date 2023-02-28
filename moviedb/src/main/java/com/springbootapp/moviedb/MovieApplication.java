package com.springbootapp.moviedb;

import com.springbootapp.moviedb.connection.ConnectionManager;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class MovieApplication {

    public static void main(String[] args) {
        TokenUsers tokenUsers = new TokenUsers();
        Timestamp timestamp = new Timestamp();

        SpringApplication.run(MovieApplication.class, args);
        ConnectionManager.connect();
        tokenUsers.key();
        timestamp.stamp();
    }
}