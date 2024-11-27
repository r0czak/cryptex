package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.dto.CryptoCurrencyPriceDTO;
import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.Trade;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.service.PriceService;
import org.atonic.cryptexsimple.service.TradeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PriceServiceImpl implements PriceService {
    private final TradeService tradeService;

    @Override
    public CryptoCurrencyPriceDTO getCurrentPrices(CryptoSymbol cryptoSymbol, LocalDateTime calculationStartTime, LocalDateTime calculationEndTime) {
        List<Trade> tradesInGivenTimeframe = tradeService.getTradesInGivenTimeframe(cryptoSymbol, calculationStartTime, calculationEndTime);
        if (tradesInGivenTimeframe.isEmpty()) {
            tradesInGivenTimeframe = tradeService.getTop3Trades(cryptoSymbol);
        }
        if (tradesInGivenTimeframe.isEmpty()) {
            tradesInGivenTimeframe = getMockTrades(cryptoSymbol);
        }

        BigDecimal calculatedPrice = tradesInGivenTimeframe.stream()
            .map(Trade::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(tradesInGivenTimeframe.size()));

        return CryptoCurrencyPriceDTO.builder()
            .cryptoId(tradesInGivenTimeframe.getFirst().getId())
            .symbol(cryptoSymbol)
            .calculatedPrice(calculatedPrice)
            .calculationStartTime(calculationStartTime)
            .calculationEndTime(calculationEndTime)
            .build();
    }

    private List<Trade> getMockTrades(CryptoSymbol cryptoSymbol) {
        BigDecimal price = switch (cryptoSymbol) {
            case BTC -> BigDecimal.valueOf(100000);
            case ETH -> BigDecimal.valueOf(3000);
            case LTC -> BigDecimal.valueOf(150);
        };

        List<Trade> trades = new ArrayList<>();
        trades.add(new Trade(0L, BigDecimal.ONE, price, LocalDateTime.now(), null, null, new Cryptocurrency(0L, cryptoSymbol)));
        trades.add(new Trade(0L, BigDecimal.ONE, price, LocalDateTime.now(), null, null, new Cryptocurrency(0L, cryptoSymbol)));
        trades.add(new Trade(0L, BigDecimal.ONE, price, LocalDateTime.now(), null, null, new Cryptocurrency(0L, cryptoSymbol)));
        return trades;
    }
}
