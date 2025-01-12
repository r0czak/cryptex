package org.atonic.cryptexsimple.init;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.FIATCurrency;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.jpa.FIATCurrencyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("prod")
@Slf4j
@AllArgsConstructor
public class ProdDataInitializer implements CommandLineRunner {
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final FIATCurrencyRepository fiatCurrencyRepository;

    @Override
    public void run(String... args) {
        try {
            log.info("Initializing production data...");

            initializeCurrencies();

            log.info("Production data initialization completed");
        } catch (Exception e) {
            log.error("Failed to initialize Production data", e);
            throw new RuntimeException("Data initialization failed", e);
        }
    }

    private void initializeCurrencies() {
        log.info("Initializing currencies...");
        if (cryptocurrencyRepository.count() == 0) {
            List<Cryptocurrency> cryptos = Arrays.asList(
                new Cryptocurrency(1L, CryptoSymbol.BTC),
                new Cryptocurrency(2L, CryptoSymbol.ETH),
                new Cryptocurrency(3L, CryptoSymbol.LTC)
            );
            cryptocurrencyRepository.saveAll(cryptos);
            log.info("Initialized cryptocurrencies: {}", cryptos);
        }
        if (fiatCurrencyRepository.count() == 0) {
            log.info("Initialized FIAT currencies: USD");
            fiatCurrencyRepository.save(new FIATCurrency(1L, FIATSymbol.USD));
        }
    }
}
