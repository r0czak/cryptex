package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("redis")
public record RedisConfigProperties(String host, int port, String password) {
}
