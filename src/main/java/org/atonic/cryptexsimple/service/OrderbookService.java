package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderbookService {
    Optional<TradeOrderPOJO> placeOrder(TradeOrderPOJO tradeOrder);

    void matchOrders(TradeOrderPOJO tradeOrder);

    List<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable);

    List<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable);
}
