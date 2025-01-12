package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

public interface ExecuteTradeProducer {
    void sendExecuteTrade(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder);

    void sendNewTradeOrder(TradeOrderPOJO tradeOrder);
}
