package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.jpa.Trade;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeService {
    void executeTrade(TradePOJO trade);

    List<Trade> getTradesInGivenTimeframe(CryptoSymbol symbol, LocalDateTime from, LocalDateTime to);

    List<Trade> getTop3Trades(CryptoSymbol symbol);
}
