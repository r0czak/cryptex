package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.config.RabbitMQConfig;
import org.atonic.cryptexsimple.exception.trade.TradeExecutionException;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.service.ExecuteTradeConsumer;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.TradeExecutionService;
import org.atonic.cryptexsimple.service.TradeOrderMatchingEngine;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ExecuteTradeConsumerImpl implements ExecuteTradeConsumer {
    private final TradeExecutionService tradeExecutionService;
    private final TradeOrderMatchingEngine tradeOrderMatchingEngine;
    private final OrderbookService orderbookService;

    @Override
    @RabbitListener(queues = RabbitMQConfig.NEW_TRADE_ORDER_QUEUE)
    public void consumeNewTradeOrder(TradeOrderPOJO tradeOrder) {
        boolean isOrderOpen = true;
        while (isOrderOpen) {
            Optional<TradeOrderPOJO> matchedOrder = tradeOrderMatchingEngine.matchTradeOrders(tradeOrder);
            if (matchedOrder.isPresent()) {
                log.info("Matched trade order: {}", matchedOrder.get());
                try {
                    TradeOrderPOJO sellOrder = tradeOrder.getType().equals(OrderType.SELL) ? tradeOrder : matchedOrder.get();
                    TradeOrderPOJO buyOrder = tradeOrder.getType().equals(OrderType.BUY) ? tradeOrder : matchedOrder.get();
                    tradeExecutionService.executeTradeWithDistributedTransaction(sellOrder, buyOrder);
                } catch (TradeExecutionException e) {
                    isOrderOpen = false;
                    log.error("Error while consuming new trade task: {}", e.getMessage());
                }
                Optional<TradeOrderPOJO> updatedOrder = orderbookService.getTradeOrder(tradeOrder.getId());
                if (updatedOrder.isPresent()) {
                    tradeOrder = updatedOrder.get();
                } else {
                    isOrderOpen = false;
                }
            } else {
                isOrderOpen = false;
            }
        }


    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.MATCHED_TRADE_ORDER_QUEUE)
    public void consumeExecuteTrade(HashMap<String, TradeOrderPOJO> matchedOrders) {
        try {
            TradeOrderPOJO sellOrder = matchedOrders.get("sellOrder");
            TradeOrderPOJO buyOrder = matchedOrders.get("buyOrder");

            log.info("Received trade execution order. Sell order: {}. Buy order: {}", sellOrder, buyOrder);

            tradeExecutionService.executeTradeWithDistributedTransaction(sellOrder, buyOrder);
        } catch (TradeExecutionException e) {
            log.error("Error while consuming trade execution task: {}", e.getMessage());
        }
    }
}
