package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderbookService {
    Optional<TradeOrderPOJO> getTradeOrder(String id);

    Optional<TradeOrderPOJO> placeOrder(TradeOrderPOJO tradeOrder);

    void removeTradeOrder(String id);

    Optional<TradeOrderPOJO> updateTradeOrder(String id, String amount, String price);
    
    List<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable);

    List<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable);

    Set<String> getTopOrdersByPrice(CryptoSymbol symbol, String price, OrderType type);
}
