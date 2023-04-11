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

    @Scheduled(fixedRate = 10800000) // запускать каждые 3 часа
    public void scheduleTask() throws Exception {
        run();
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 50; i++) {
            URL url = parserMovies.createUrl(String.format("https://kinobd.ru/api/films?page=%d", i));
            String resultJson = parserMovies.parseUrl(url);
            parserMovies.parseMovie(resultJson);
        }
    }
}

//https://kinobd.ru/api/films?page=17739

