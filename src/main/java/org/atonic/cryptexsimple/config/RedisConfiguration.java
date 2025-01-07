package org.atonic.cryptexsimple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {
    @Bean
    public JedisConnectionFactory redisConnectionFactory(RedisConfigProperties redisConfigProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConfigProperties.host());
        redisStandaloneConfiguration.setPort(redisConfigProperties.port());
        redisStandaloneConfiguration.setPassword(redisConfigProperties.password());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
