package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.jpa.Trade;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeService {
    void executeTrade(TradePOJO trade);

    Page<Trade> getUserTrades(User user,
                              CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol,
                              OrderType orderType,
                              LocalDateTime from, LocalDateTime to,
                              Pageable pageRequest);

    List<Trade> getTradesInGivenTimeframe(CryptoSymbol symbol, LocalDateTime from, LocalDateTime to);

    List<Trade> getTop3Trades(CryptoSymbol symbol);
}
