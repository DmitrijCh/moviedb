package com.springbootapp.moviedb;

import com.springbootapp.moviedb.connection.Connection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;


@SpringBootApplication
public class MoviedbApplication {

    public static String URL = "https://api.themoviedb.org/3/discover/movie?api_key=743b8194cfdd5e009a3264a5813d2a6d&page=" + page(5);

    public static void main(String[] args) {
        SpringApplication.run(MoviedbApplication.class, args);
        Connection.connect();
        // создаем URL из строки
        URL url = MoviedbApplication.createUrl(URL);
            // загружаем Json в виде Java строки
            String resultJson = MoviedbApplication.parseUrl(url);
            // парсим полученный JSON и печатаем его на экран
            MoviedbApplication.parseMovie(resultJson);
    }

    // открываем соедиение к URL
    public static String parseUrl(URL url) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            // построчно считываем результат в объект StringBuilder
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // парсим данные о фильах
    public static void parseMovie(String resultJson) {
        try {
            // конвертируем строку с Json в JSONObject для дальнейшего его парсинга
            JSONObject movieJsonObject = (JSONObject) JSONValue.parseWithException(resultJson);
            // получаем массив элементов для results
            JSONArray movieArray = (JSONArray) movieJsonObject.get("results");
            JSONArray movie = (JSONArray) movieJsonObject.get("results");
            // достаем из массива элементы
            System.out.println();

            int size = 19;
            for (int n = 0; n <= size; n++) {
                JSONObject movieTitle = (JSONObject) movieArray.get(n);
                System.out.println(movieTitle.get("title"));
                System.out.println(movieTitle.get("poster_path"));
                System.out.println();

                Connection connection = new Connection();
                String query = "INSERT INTO table_name (poster_path, title) VALUES (?, ?)";
                try {
                    PreparedStatement statement = Connection.connection.prepareStatement(query);
                    statement.setString(1, (String) movieTitle.get("poster_path"));
                    statement.setString(2, (String) movieTitle.get("title"));
                    statement.execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    // создаем объект URL из указанной в параметре строки
    public static URL createUrl(String link) {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int page(int a) {
        int z = 0;
        int u;
        for( u = 1; u <= a; u++ ) {
            System.out.println(z = u);
        }
        System.out.println(z);
        return z;
    }

}

//	public static void time() {
//		Runnable runnable = new Runnable() {
//			public void run() {
//				System.out.println("Hello");
//			}
//		};
//		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//		executor.scheduleAtFixedRate(runnable,0,1,TimeUnit.SECONDS);
//	}
//}
