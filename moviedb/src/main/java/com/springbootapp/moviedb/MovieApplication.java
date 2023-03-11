package com.springbootapp.moviedb;

import com.springbootapp.moviedb.connection.ConnectionManager;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.sql.SQLException;

@ComponentScan
@EnableAutoConfiguration
public class MovieApplication {

    public static void main(String[] args) throws SQLException {
        TokenUsers tokenUsers = new TokenUsers();
        Timestamp timestamp = new Timestamp();
        ConnectionManager connectionManager = new ConnectionManager();

        SpringApplication.run(MovieApplication.class, args);
        connectionManager.connect();
        tokenUsers.key();
        timestamp.stamp();
    }
}
