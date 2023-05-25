package com.springbootapp.moviedb.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ParserMovies {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ParserMovies(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public void parseMovie(String resultJson) throws ParseException {
        JSONObject movieJsonObject = (JSONObject) JSONValue.parseWithException(resultJson);
        JSONArray movieArray = (JSONArray) movieJsonObject.get("data");

        int size = 49;
        for (int n = 0; n <= size; n++) {
            JSONObject movieTitle = (JSONObject) movieArray.get(n);
            int id = ((Number) movieTitle.get("id")).intValue();

            String queryCheck = "SELECT COUNT(*) FROM movies WHERE id = ?";
            int count = jdbcTemplate.queryForObject(queryCheck, Integer.class, id);

            if (count == 0) {
                String queryInsert = "INSERT INTO movies (id, name_original, name, year, time, age_restriction, description, slogan, budget, country_ru, type, created_at, updated_at, poster) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(queryInsert, id, movieTitle.get("name_original"), movieTitle.get("name_russian"), movieTitle.get("year"), movieTitle.get("time"), movieTitle.get("age_restriction"), movieTitle.get("description"), movieTitle.get("slogan"), movieTitle.get("budget"), movieTitle.get("country_ru"), movieTitle.get("type"), movieTitle.get("created_at"), movieTitle.get("updated_at"), movieTitle.get("small_poster"));
            }
        }
    }

    public URL createUrl(String link) throws MalformedURLException {
        return new URL(link);
    }
}
