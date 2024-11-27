package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.Trade;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.TradeRepository;
import org.atonic.cryptexsimple.service.TradeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final TradeRepository tradeRepository;

    @Override
    public List<Trade> getTradesInGivenTimeframe(CryptoSymbol symbol, LocalDateTime from, LocalDateTime to) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(symbol);
        if (crypto.isEmpty()) {
            throw new IllegalArgumentException("Cryptocurrency with symbol " + symbol + " does not exist");
        }

        return tradeRepository.findAllByCryptocurrencyAndTimestampBetween(crypto.get(), from, to);
    }

    @Override
    public List<Trade> getTop3Trades(CryptoSymbol symbol) {
        Optional<Cryptocurrency> crypto = cryptocurrencyRepository.findBySymbol(symbol);
        if (crypto.isEmpty()) {
            throw new IllegalArgumentException("Cryptocurrency with symbol " + symbol + " does not exist");
        }

        return tradeRepository.findTop3ByCryptocurrencyOrderByTimestamp(crypto.get());
    }
}
