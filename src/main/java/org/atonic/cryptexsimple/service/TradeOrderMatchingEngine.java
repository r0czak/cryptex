package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

import java.util.Optional;

public interface TradeOrderMatchingEngine {
    Optional<TradeOrderPOJO> matchTradeOrders(TradeOrderPOJO tradeOrder);
}
