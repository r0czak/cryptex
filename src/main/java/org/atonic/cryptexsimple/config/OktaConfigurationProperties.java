package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("okta")
public record OktaConfigurationProperties(
    String issuer,
    String audience,
    String client_id,
    String client_secret,
    String redirect_uri,
    String post_logout_redirect_uri
    ) {
}
