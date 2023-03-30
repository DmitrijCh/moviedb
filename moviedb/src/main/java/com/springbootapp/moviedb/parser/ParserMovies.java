package com.springbootapp.moviedb.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ParserMovies {

    private final Connection connection;

    @Autowired
    public ParserMovies(Connection connection) {
        this.connection = connection;
    }

    public String parseUrl(URL url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            stringBuilder.append(inputLine);
        }
        return stringBuilder.toString();
    }

    public void parseMovie(String resultJson) throws ParseException, SQLException {
        JSONObject movieJsonObject = (JSONObject) JSONValue.parseWithException(resultJson);
        JSONArray movieArray = (JSONArray) movieJsonObject.get("data");
        System.out.println();

        int size = 49;
        for (int n = 0; n <= size; n++) {
            JSONObject movieTitle = (JSONObject) movieArray.get(n);
            String nameRussian = (String) movieTitle.get("name_russian");

            String queryCheck = "SELECT COUNT(*) FROM movies WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(queryCheck);
            statement.setString(1, nameRussian);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String queryInsert = "INSERT INTO movies (name, year, poster) VALUES (?, ?, ?)";
                PreparedStatement statementInsert = connection.prepareStatement(queryInsert);
                statementInsert.setString(1, nameRussian);
                statementInsert.setString(2, (String) movieTitle.get("year"));
                statementInsert.setString(3, (String) movieTitle.get("big_poster"));
                statementInsert.execute();
            }
        }
    }

    public URL createUrl(String link) throws MalformedURLException {
        return new URL(link);
    }
}
