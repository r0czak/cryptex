package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

import java.util.HashMap;

public interface ExecuteTradeConsumer {
    void consumeNewTradeOrder(TradeOrderPOJO tradeOrder);

    void consumeExecuteTrade(HashMap<String, TradeOrderPOJO> matchedOrders);
}
