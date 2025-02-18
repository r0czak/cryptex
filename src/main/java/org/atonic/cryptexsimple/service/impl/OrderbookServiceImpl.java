package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.repository.redis.RedisTradeOrderRepository;
import org.atonic.cryptexsimple.service.ExecuteTradeProducer;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

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
    public Page<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        String key = getTradeOrdersRedisKey(OrderType.BUY, symbol);
        Set<String> buyOrders = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, pageable.getOffset(), pageable.getPageSize());
        assert buyOrders != null;

        List<TradeOrderPOJO> tradeOrders = StreamSupport
            .stream(tradeOrderRepository.findAllById(buyOrders.stream().toList()).spliterator(), false)
            .toList();

        return new PageImpl<>(
            tradeOrders,
            pageable,
            tradeOrders.size()
        );
    }

    @Override
    public Page<TradeOrderPOJO> getBuyTradeOrdersForeign(CryptoSymbol symbol, Long userId, Pageable pageable) {
        String key = getTradeOrdersRedisKey(OrderType.BUY, symbol);
        Set<String> buyOrders = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, 0, 100);
        assert buyOrders != null;

        List<TradeOrderPOJO> tradeOrders = StreamSupport
            .stream(tradeOrderRepository.findAllById(buyOrders.stream()
                    .toList())
                .spliterator(), false)
            .filter(tradeOrder -> !tradeOrder.getUserId().equals(userId.toString()))
            .toList();

        return new PageImpl<>(
            tradeOrders,
            pageable,
            tradeOrders.size()
        );
    }

    @Override
    public Page<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        String key = getTradeOrdersRedisKey(OrderType.SELL, symbol);
        Set<String> sellOrders = redisTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE, pageable.getOffset(), pageable.getPageSize());
        assert sellOrders != null;

        List<TradeOrderPOJO> tradeOrders = StreamSupport
            .stream(tradeOrderRepository.findAllById(sellOrders.stream().toList()).spliterator(), false)
            .toList();

        return new PageImpl<>(
            tradeOrders,
            pageable,
            tradeOrders.size()
        );
    }

    @Override
    public Page<TradeOrderPOJO> getSellTradeOrdersForeign(CryptoSymbol symbol, Long userId, Pageable pageable) {
        String key = getTradeOrdersRedisKey(OrderType.SELL, symbol);
        Set<String> sellOrders = redisTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE, 0, 100);
        assert sellOrders != null;

        List<TradeOrderPOJO> tradeOrders = StreamSupport
            .stream(tradeOrderRepository.findAllById(sellOrders.stream()
                    .toList())
                .spliterator(), false)
            .filter(tradeOrder -> !tradeOrder.getUserId().equals(userId.toString()))
            .toList();

        return new PageImpl<>(
            tradeOrders,
            pageable,
            tradeOrders.size()
        );
    }

    @Override
    public Set<String> getTopOrdersByPrice(CryptoSymbol symbol, String price, OrderType type) {
        String key = getTradeOrdersRedisKey(type, symbol);

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
