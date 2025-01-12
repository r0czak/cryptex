package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dev")
public record DevConfigProperties(
    String auth0Id1,
    String email1,
    String auth0Id2,
    String email2
) {
}
