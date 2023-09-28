package com.springbootapp.moviedb.message;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaMessage {

    private final Producer<String, String> producer;

    public KafkaMessage() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String topic, String message) {
        long timestamp = System.currentTimeMillis();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, timestamp, null, message);
        producer.send(record);
    }
}