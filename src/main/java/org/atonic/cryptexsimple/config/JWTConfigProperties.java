package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JWTConfigProperties(String  jwt_secret) {
}
