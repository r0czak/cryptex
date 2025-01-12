package org.atonic.cryptexsimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq")
public record RabbitMQConfigProperties(String host, String amqpPort, String username,
                                       String password) {
}
