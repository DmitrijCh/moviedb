package com.springbootapp.moviedb.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @KafkaListener(topics = "Test")
    public void consumeMessage(String message) {
        System.out.println("Получено сообщение из Kafka: " + message);
    }
}

