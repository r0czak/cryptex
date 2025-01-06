package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dev")
public record DevConfigProperties(
    String auth0Id,
    String email
) {
}
