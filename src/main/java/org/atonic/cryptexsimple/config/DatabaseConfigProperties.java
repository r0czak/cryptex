package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("database")
public record DatabaseConfigProperties(String mysql_username, String mysql_password) {
}
