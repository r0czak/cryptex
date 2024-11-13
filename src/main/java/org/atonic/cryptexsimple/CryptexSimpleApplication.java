package org.atonic.cryptexsimple;

import org.atonic.cryptexsimple.config.DatabaseConfigProperties;
import org.atonic.cryptexsimple.config.JWTConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    DatabaseConfigProperties.class,
    JWTConfigProperties.class
})
public class CryptexSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptexSimpleApplication.class, args);
    }

}
