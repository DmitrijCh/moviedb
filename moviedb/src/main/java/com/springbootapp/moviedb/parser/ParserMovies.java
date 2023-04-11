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

            int id = ((Number) movieTitle.get("id")).intValue();

            String queryCheck = "SELECT COUNT(*) FROM movies WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(queryCheck);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String queryInsert = "INSERT INTO movies (id, name_original, name, year, time, age_restriction, description, slogan, budget, country_ru, type, created_at, updated_at, poster) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statementInsert = connection.prepareStatement(queryInsert);
                statementInsert.setInt(1, id);
                statementInsert.setString(2, (String) movieTitle.get("name_original"));
                statementInsert.setString(3, (String) movieTitle.get("name_russian"));
                statementInsert.setString(4, (String) movieTitle.get("year"));
                statementInsert.setString(5, (String) movieTitle.get("time"));
                statementInsert.setString(6, (String) movieTitle.get("age_restriction"));
                statementInsert.setString(7, (String) movieTitle.get("description"));
                statementInsert.setString(8, (String) movieTitle.get("slogan"));
                statementInsert.setString(9, (String) movieTitle.get("budget"));
                statementInsert.setString(10, (String) movieTitle.get("country_ru"));
                statementInsert.setString(11, (String) movieTitle.get("type"));
                statementInsert.setString(12, (String) movieTitle.get("created_at"));
                statementInsert.setString(13, (String) movieTitle.get("updated_at"));
                statementInsert.setString(14, (String) movieTitle.get("small_poster"));
                statementInsert.execute();
            }
        }
    }

    public URL createUrl(String link) throws MalformedURLException {
        return new URL(link);
    }
}
