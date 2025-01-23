package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface OrderbookService {
    Optional<TradeOrderPOJO> getTradeOrder(String id);

    Optional<TradeOrderPOJO> placeOrder(TradeOrderPOJO tradeOrder);

    void removeTradeOrder(String id);

    Optional<TradeOrderPOJO> updateTradeOrder(String id, String amount, String price);

    Page<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable);

    Page<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable);

    Set<String> getTopOrdersByPrice(CryptoSymbol symbol, String price, OrderType type);
}
