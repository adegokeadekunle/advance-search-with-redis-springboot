package com.adekunle.advancesearchusingredis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisConfig {

    private String host;
    private Integer port;

    public JedisPooled jedisPooled() {
        return new JedisPooled(host,port);
    }
}
