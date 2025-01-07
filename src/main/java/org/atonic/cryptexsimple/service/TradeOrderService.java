package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.jpa.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.jpa.TradeOrder;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.util.List;
import java.util.Optional;

public interface TradeOrderService {
    Optional<TradeOrder> placeOrder(TradeOrder tradeOrder);

    void matchOrders(Cryptocurrency cryptocurrency, User user);

    List<TradeOrder> getOpenTradeOrders(CryptoSymbol symbol);
}
