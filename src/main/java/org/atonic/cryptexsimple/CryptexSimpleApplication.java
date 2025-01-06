package org.atonic.cryptexsimple;

import org.atonic.cryptexsimple.config.DatabaseConfigProperties;
import org.atonic.cryptexsimple.config.DevConfigProperties;
import org.atonic.cryptexsimple.config.OktaConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    DatabaseConfigProperties.class,
    OktaConfigurationProperties.class,
    DevConfigProperties.class
})
public class CryptexSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptexSimpleApplication.class, args);
    }

}
