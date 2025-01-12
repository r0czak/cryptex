package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.config.RabbitMQConfig;
import org.atonic.cryptexsimple.exception.amqp.AMQPMessagePublishException;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.service.ExecuteTradeProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@AllArgsConstructor
public class ExecuteTradeProducerImpl implements ExecuteTradeProducer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNewTradeOrder(TradeOrderPOJO tradeOrder) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADE_ORDER_EXCHANGE,
                RabbitMQConfig.NEW_TRADE_ORDER_ROUTING_KEY,
                tradeOrder
            );
            log.info("New trade order sent to RabbitMQ: {}", tradeOrder);
        } catch (Exception e) {
            log.error("Error sending new trade order to RabbitMQ: {}", e.getMessage());
            throw new AMQPMessagePublishException("Error sending new trade order to RabbitMQ", e);
        }
    }

    @Override
    public void sendExecuteTrade(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) {
        HashMap<String, TradeOrderPOJO> trade = new HashMap<>();
        trade.put("sellOrder", sellOrder);
        trade.put("buyOrder", buyOrder);
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADE_ORDER_EXCHANGE,
                RabbitMQConfig.MATCHED_TRADE_ORDER_ROUTING_KEY,
                trade
            );
            log.info("Trade order sent to RabbitMQ: {}", trade);
        } catch (Exception e) {
            log.error("Error sending trade order to RabbitMQ: {}", e.getMessage());
            throw new AMQPMessagePublishException("Error sending trade order to RabbitMQ", e);
        }
    }
}
