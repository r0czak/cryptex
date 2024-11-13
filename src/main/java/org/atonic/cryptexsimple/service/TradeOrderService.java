package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.Trade;
import org.atonic.cryptexsimple.model.entity.TradeOrder;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.util.List;
import java.util.Optional;

public interface TradeOrderService {
    Optional<TradeOrder> placeOrder(TradeOrder tradeOrder);
    void matchOrders(Cryptocurrency cryptocurrency, User user);
    List<TradeOrder> getOpenTradeOrders(CryptoSymbol symbol);
}
