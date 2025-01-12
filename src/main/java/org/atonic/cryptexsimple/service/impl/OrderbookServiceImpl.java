package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.repository.redis.RedisTradeOrderRepository;
import org.atonic.cryptexsimple.service.ExecuteTradeProducer;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class OrderbookServiceImpl implements OrderbookService {
    private final ExecuteTradeProducer executeTradeProducer;

    private final StringRedisTemplate redisTemplate;
    private final RedisTradeOrderRepository tradeOrderRepository;

    private static final String BUY_ORDERS_KEY = "orderbook:%s:buy";
    private static final String SELL_ORDERS_KEY = "orderbook:%s:sell";

    @Override
    public Optional<TradeOrderPOJO> getTradeOrder(String id) {
        return tradeOrderRepository.findById(id);
    }

    @Override
    public Optional<TradeOrderPOJO> placeOrder(TradeOrderPOJO tradeOrder) {
        TradeOrderPOJO savedOrder = tradeOrderRepository.save(tradeOrder);

        String key = getTradeOrdersRedisKey(savedOrder.getType(), savedOrder.getCryptoSymbol());

        redisTemplate.opsForZSet().add(key, savedOrder.getId(), new BigDecimal(savedOrder.getPrice()).doubleValue());
        executeTradeProducer.sendNewTradeOrder(savedOrder);

        return Optional.of(savedOrder);
    }

    @Override
    public void removeTradeOrder(String id) {
        Optional<TradeOrderPOJO> tradeOrder = getTradeOrder(id);
        if (tradeOrder.isEmpty()) {
            return;
        }
        String key = getTradeOrdersRedisKey(tradeOrder.get().getType(), tradeOrder.get().getCryptoSymbol());
        redisTemplate.opsForZSet().remove(key, id);
        tradeOrderRepository.deleteById(id);
    }

    @Override
    public Optional<TradeOrderPOJO> updateTradeOrder(String id, String amount, String price) {
        Optional<TradeOrderPOJO> tradeOrder = getTradeOrder(id);
        if (tradeOrder.isPresent()) {
            if (new BigDecimal(price).compareTo(new BigDecimal(tradeOrder.get().getPrice())) != 0) {
                String key = getTradeOrdersRedisKey(tradeOrder.get().getType(), tradeOrder.get().getCryptoSymbol());
                redisTemplate.opsForZSet().remove(key, id);
                redisTemplate.opsForZSet().add(key, id, new BigDecimal(price).doubleValue());
            }
            tradeOrder.get().setAmount(amount);
            tradeOrder.get().setPrice(price);
            tradeOrderRepository.save(tradeOrder.get());
            return tradeOrder;
        }
        return Optional.empty();
    }

    @Override
    public List<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        return tradeOrderRepository.findByCryptoSymbolAndTypeOrderByPriceDesc(symbol, OrderType.BUY, pageable);
    }

    @Override
    public List<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        return tradeOrderRepository.findByCryptoSymbolAndTypeOrderByPriceDesc(symbol, OrderType.SELL, pageable);
    }

    @Override
    public Set<String> getTopOrdersByPrice(CryptoSymbol symbol, String price, OrderType type) {
        String key = type == OrderType.BUY
            ? String.format(BUY_ORDERS_KEY, symbol)
            : String.format(SELL_ORDERS_KEY, symbol);

        return OrderType.BUY.equals(type) ?
            redisTemplate.opsForZSet().reverseRangeByScore(key, new BigDecimal(price).doubleValue(), Double.MAX_VALUE) :
            redisTemplate.opsForZSet().rangeByScore(key, 0.0, new BigDecimal(price).doubleValue());
    }

    private String getTradeOrdersRedisKey(OrderType type, CryptoSymbol symbol) {
        return type == OrderType.BUY
            ? String.format(BUY_ORDERS_KEY, symbol)
            : String.format(SELL_ORDERS_KEY, symbol);
    }
}
