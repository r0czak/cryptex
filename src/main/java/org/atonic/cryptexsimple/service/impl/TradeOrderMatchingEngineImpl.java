package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.TradeOrderMatchingEngine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class TradeOrderMatchingEngineImpl implements TradeOrderMatchingEngine {
    private final OrderbookService orderbookService;

    public Optional<TradeOrderPOJO> matchTradeOrders(TradeOrderPOJO tradeOrder) {
        if (OrderType.SELL.equals(tradeOrder.getType())) {
            Set<String> topBuyIdsByPrice = orderbookService.getTopOrdersByPrice(tradeOrder.getCryptoSymbol(), tradeOrder.getPrice(), OrderType.BUY);
            if (topBuyIdsByPrice == null || topBuyIdsByPrice.isEmpty()) {
                return Optional.empty();
            }
            return matchSellOrder(tradeOrder, topBuyIdsByPrice);
        } else {
            Set<String> topSellIdsByPrice = orderbookService.getTopOrdersByPrice(tradeOrder.getCryptoSymbol(), tradeOrder.getPrice(), OrderType.SELL);
            if (topSellIdsByPrice == null || topSellIdsByPrice.isEmpty()) {
                return Optional.empty();
            }
            return matchBuyOrder(tradeOrder, topSellIdsByPrice);
        }
    }

    private Optional<TradeOrderPOJO> matchSellOrder(TradeOrderPOJO sellOrder, Set<String> topBuyIdsByPrice) {
        for (String buyOrderId : topBuyIdsByPrice) {
            Optional<TradeOrderPOJO> buyOrder = orderbookService.getTradeOrder(buyOrderId);
            if (buyOrder.isPresent() &&
                new BigDecimal(sellOrder.getPrice()).compareTo(new BigDecimal(buyOrder.get().getPrice())) <= 0 &&
                !sellOrder.getUserId().equals(buyOrder.get().getUserId())) {
                return buyOrder;
            }
        }
        return Optional.empty();
    }

    private Optional<TradeOrderPOJO> matchBuyOrder(TradeOrderPOJO buyOrder, Set<String> topSellIdsByPrice) {
        for (String sellOrderId : topSellIdsByPrice) {
            Optional<TradeOrderPOJO> sellOrder = orderbookService.getTradeOrder(sellOrderId);
            if (sellOrder.isPresent() &&
                new BigDecimal(buyOrder.getPrice()).compareTo(new BigDecimal(sellOrder.get().getPrice())) <= 0 &&
                !buyOrder.getUserId().equals(sellOrder.get().getUserId())) {
                return sellOrder;
            }
        }
        return Optional.empty();
    }
}
