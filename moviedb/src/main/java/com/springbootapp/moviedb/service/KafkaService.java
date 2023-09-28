package com.springbootapp.moviedb.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class KafkaService {

    private final JdbcTemplate jdbcTemplate;
    private final AtomicLong messageCount = new AtomicLong(0);
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    public KafkaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = "Test")
    public void consumeMessage(ConsumerRecord<String, String> record) {
        long messageTimestamp = record.timestamp();

        LocalDateTime messageDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(messageTimestamp),
                ZoneId.systemDefault()
        );

        System.out.println(record.value());

        if (messageDateTime.getMinute() == currentDateTime.getMinute()) {
            messageCount.incrementAndGet();
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void processMessages() {
        long count = messageCount.getAndSet(0);

        if (count > 0) {
            String query = "INSERT INTO like_data (day, like_count) VALUES (?, ?) " +
                    "ON CONFLICT (day) DO UPDATE SET like_count = like_data.like_count + ?";
            jdbcTemplate.update(query, currentDateTime, count, count);
        }
    }
}