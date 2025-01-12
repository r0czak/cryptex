package org.atonic.cryptexsimple;

import org.atonic.cryptexsimple.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    DatabaseConfigProperties.class,
    RedisConfigProperties.class,
    OktaConfigurationProperties.class,
    DevConfigProperties.class,
    RabbitMQConfigProperties.class
})
public class CryptexSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptexSimpleApplication.class, args);
    }

}
