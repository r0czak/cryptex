package org.atonic.cryptexsimple.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String MATCHED_TRADE_ORDER_QUEUE = "matched-trade-order-queue";
    public static final String NEW_TRADE_ORDER_QUEUE = "new-trade-order-queue";

    public static final String TRADE_ORDER_EXCHANGE = "trade-order-exchange";

    public static final String MATCHED_TRADE_ORDER_ROUTING_KEY = "trade.order.matched";
    public static final String NEW_TRADE_ORDER_ROUTING_KEY = "trade.order.new";

    @Bean
    public Queue matchedTradeOrderQueue() {
        return new Queue(MATCHED_TRADE_ORDER_QUEUE, true);
    }

    @Bean
    public Queue newTradeOrderQueue() {
        return new Queue(NEW_TRADE_ORDER_QUEUE, true);
    }

    @Bean
    public TopicExchange tradeOrderExchange() {
        return new TopicExchange(TRADE_ORDER_EXCHANGE);
    }

    @Bean
    public Binding matchedTradeOrderBinding(Queue matchedTradeOrderQueue, TopicExchange tradeOrderExchange) {
        return BindingBuilder
            .bind(matchedTradeOrderQueue)
            .to(tradeOrderExchange)
            .with(MATCHED_TRADE_ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding newTradeOrderBinding(Queue newTradeOrderQueue, TopicExchange tradeOrderExchange) {
        return BindingBuilder
            .bind(newTradeOrderQueue)
            .to(tradeOrderExchange)
            .with(NEW_TRADE_ORDER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
