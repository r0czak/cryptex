package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.exception.trade.TradeExecutionException;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

public interface TradeExecutionService {
    void executeTradeWithDistributedTransaction(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) throws TradeExecutionException;
}
