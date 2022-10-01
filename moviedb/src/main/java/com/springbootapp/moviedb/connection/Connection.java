package com.springbootapp.moviedb.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connection {

    public static java.sql.Connection connection;

    FileInputStream fis;
    Properties property = new Properties();

    public void ConnectionManager(){
        try {
            fis = new FileInputStream("src/main/resources/application.properties");
            property.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "dima";
            String password = "193105610";
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
