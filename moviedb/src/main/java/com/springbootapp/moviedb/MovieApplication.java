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

    @Scheduled(fixedRate = 10800000)
    public void scheduleTask() throws Exception {
        run();
    }

    @Override
    public void run(String... args) throws Exception {
        int batchSize = 10;
        int totalQuestions = 17739;
        int numBatches = (int) Math.ceil((double) totalQuestions / batchSize);
        for (int i = 0; i < numBatches; i++) {
            int start = i * batchSize + 1;
            int end = Math.min(start + batchSize - 1, totalQuestions);
            for (int j = start; j <= end; j++) {
                URL url = parserMovies.createUrl(String.format("https://kinobd.ru/api/films?page=%d", j));
                String resultJson = parserMovies.parseUrl(url);
                parserMovies.parseMovie(resultJson);
            }
            Thread.sleep(1000);
        }
    }
}

