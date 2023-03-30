package com.springbootapp.moviedb;

import com.springbootapp.moviedb.parser.ParserMovies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;

@SpringBootApplication
@EnableScheduling
public class MovieApplication implements CommandLineRunner {
    private final ParserMovies parserMovies;

    @Autowired
    public MovieApplication(ParserMovies parserMovies) {
        this.parserMovies = parserMovies;
    }

    public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);
    }

    @Scheduled(fixedRate = 60000) // запускать каждую 1 минуту
    public void scheduleTask() throws Exception {
        run();
    }

    @Override
    public void run(String... args) throws Exception {
        String URL = "https://kinobd.ru/api/films";
        URL url = parserMovies.createUrl(URL);
        String resultJson = parserMovies.parseUrl(url);
        parserMovies.parseMovie(resultJson);
    }
}



