package com.ohzzi.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    private final EmbeddedRedisConfig redisConfig;

    public RedisConfig(final EmbeddedRedisConfig config) {
        this.redisConfig = config;
    }

    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create();
    }
}
