package com.springbootapp.moviedb.message;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JedisMessage {
    private final JedisPool jedisPool;

    public JedisMessage() {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
    }

    public void setKey(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, 10L);
        }
    }

    public void deleteExpiredKeys() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> expiredKeys = jedis.keys("*").stream()
                    .filter(k -> jedis.ttl(k) < 0)
                    .collect(Collectors.toSet());
            if (!expiredKeys.isEmpty()) {
                jedis.del(expiredKeys.toArray(new String[0]));
            }
        }
    }

    public Long getKeyCount() {
        try (Jedis jedis = jedisPool.getResource()) {
            return (long) jedis.keys("*").size();
        }
    }
}

