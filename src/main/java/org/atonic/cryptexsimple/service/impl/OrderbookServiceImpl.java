package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.atonic.cryptexsimple.model.repository.redis.RedisTradeOrderRepository;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.TradeService;
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
    private final TradeService tradeService;

    private final StringRedisTemplate redisTemplate;
    private final RedisTradeOrderRepository tradeOrderRepository;

    private static final String BUY_ORDERS_KEY = "orderbook:%s:buy";
    private static final String SELL_ORDERS_KEY = "orderbook:%s:sell";

    @Override
    public Optional<TradeOrderPOJO> placeOrder(TradeOrderPOJO tradeOrder) {
        TradeOrderPOJO savedOrder = tradeOrderRepository.save(tradeOrder);

        String key = tradeOrder.getType() == OrderType.BUY
            ? String.format(BUY_ORDERS_KEY, tradeOrder.getCryptoSymbol())
            : String.format(SELL_ORDERS_KEY, tradeOrder.getCryptoSymbol());

        redisTemplate.opsForZSet().add(key, savedOrder.getId(), new BigDecimal(savedOrder.getPrice()).doubleValue());
        matchOrders(savedOrder);

        return Optional.of(savedOrder);
    }

    @Override
    public void matchOrders(TradeOrderPOJO tradeOrder) {
        if (OrderType.SELL.equals(tradeOrder.getType())) {
            Set<String> topBuyIdsByPrice = getTopOrdersByPrice(tradeOrder.getCryptoSymbol(), tradeOrder.getPrice(), OrderType.BUY);
            if (topBuyIdsByPrice == null || topBuyIdsByPrice.isEmpty()) {
                return;
            }

            matchSellOrder(tradeOrder, topBuyIdsByPrice);
        } else {
            Set<String> topSellIdsByPrice = getTopOrdersByPrice(tradeOrder.getCryptoSymbol(), tradeOrder.getPrice(), OrderType.SELL);
            if (topSellIdsByPrice == null || topSellIdsByPrice.isEmpty()) {
                return;
            }

            matchBuyOrder(tradeOrder, topSellIdsByPrice);
        }
    }

    @Override
    public List<TradeOrderPOJO> getBuyTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        return tradeOrderRepository.findByCryptoSymbolAndTypeOrderByPriceDesc(symbol, OrderType.BUY, pageable);
    }

    @Override
    public List<TradeOrderPOJO> getSellTradeOrders(CryptoSymbol symbol, Pageable pageable) {
        return tradeOrderRepository.findByCryptoSymbolAndTypeOrderByPriceDesc(symbol, OrderType.SELL, pageable);
    }

    private void matchSellOrder(TradeOrderPOJO sellOrder, Set<String> topBuyIdsByPrice) {
        for (String buyId : topBuyIdsByPrice) {
            Optional<TradeOrderPOJO> buyOrder = tradeOrderRepository.findById(buyId);
            if (buyOrder.isEmpty()) {
                continue;
            }

            if (new BigDecimal(sellOrder.getPrice()).compareTo(new BigDecimal(buyOrder.get().getPrice())) <= 0
                && !sellOrder.getUserId().equals(buyOrder.get().getUserId())) {

                executeTrade(sellOrder, buyOrder.get());
                Optional<TradeOrderPOJO> sellOrderOptional = tradeOrderRepository.findById(sellOrder.getId());
                if (sellOrderOptional.isEmpty()) {
                    return;
                } else {
                    sellOrder = sellOrderOptional.get();
                }
            }
        }
    }

    private void matchBuyOrder(TradeOrderPOJO buyOrder, Set<String> topSellIdsByPrice) {
        for (String sellId : topSellIdsByPrice) {
            Optional<TradeOrderPOJO> sellOrder = tradeOrderRepository.findById(sellId);
            if (sellOrder.isEmpty()) {
                continue;
            }

            if (new BigDecimal(buyOrder.getPrice()).compareTo(new BigDecimal(sellOrder.get().getPrice())) >= 0
                && !buyOrder.getUserId().equals(sellOrder.get().getUserId())) {

                executeTrade(sellOrder.get(), buyOrder);
                Optional<TradeOrderPOJO> buyOrderOptional = tradeOrderRepository.findById(buyOrder.getId());
                if (buyOrderOptional.isEmpty()) {
                    return;
                } else {
                    buyOrder = buyOrderOptional.get();
                }
            }
        }
    }

    private void executeTrade(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) {
        BigDecimal sellAmount = new BigDecimal(sellOrder.getAmount());
        BigDecimal buyAmount = new BigDecimal(buyOrder.getAmount());
        BigDecimal tradeAmount = sellAmount.min(buyAmount);

        sellOrder.setAmount(sellAmount.subtract(tradeAmount).toString());
        buyOrder.setAmount(buyAmount.subtract(tradeAmount).toString());

        TradePOJO tradePOJO = prepareTradePOJO(sellOrder, buyOrder);

        if (new BigDecimal(sellOrder.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
            tradeOrderRepository.delete(sellOrder);
            redisTemplate.opsForZSet().remove(String.format(SELL_ORDERS_KEY, sellOrder.getCryptoSymbol()), sellOrder.getId());
        } else {
            tradeOrderRepository.save(sellOrder);
        }

        if (new BigDecimal(buyOrder.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
            tradeOrderRepository.delete(buyOrder);
            redisTemplate.opsForZSet().remove(String.format(BUY_ORDERS_KEY, buyOrder.getCryptoSymbol()), buyOrder.getId());
        } else {
            tradeOrderRepository.save(buyOrder);
        }

        tradeService.executeTrade(tradePOJO);

        log.info("Trade orders executed: {} {} for {} {}", tradeAmount, sellOrder.getCryptoSymbol(), tradeAmount.multiply(new BigDecimal(sellOrder.getPrice())), sellOrder.getFiatSymbol());
    }

    private Set<String> getTopOrdersByPrice(CryptoSymbol symbol, String price, OrderType type) {
        String key = type == OrderType.BUY
            ? String.format(BUY_ORDERS_KEY, symbol)
            : String.format(SELL_ORDERS_KEY, symbol);

        return OrderType.BUY.equals(type) ?
            redisTemplate.opsForZSet().reverseRangeByScore(key, new BigDecimal(price).doubleValue(), Double.MAX_VALUE) :
            redisTemplate.opsForZSet().rangeByScore(key, 0.0, new BigDecimal(price).doubleValue());
    }

    private TradePOJO prepareTradePOJO(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) {
        BigDecimal sellAmount = new BigDecimal(sellOrder.getAmount());
        BigDecimal buyAmount = new BigDecimal(buyOrder.getAmount());
        BigDecimal tradeAmount = sellAmount.min(buyAmount);

        BigDecimal tradePrice = new BigDecimal(sellOrder.getPrice());

        return TradePOJO.builder()
            .amount(tradeAmount)
            .price(tradePrice)
            .sellerId(Long.valueOf(sellOrder.getUserId()))
            .sellerFIATWalletId(Long.valueOf(sellOrder.getFiatWalletId()))
            .sellerCryptoWalletId(Long.valueOf(sellOrder.getCryptoWalletId()))
            .buyerId(Long.valueOf(buyOrder.getUserId()))
            .buyerFIATWalletId(Long.valueOf(buyOrder.getFiatWalletId()))
            .buyerCryptoWalletId(Long.valueOf(buyOrder.getCryptoWalletId()))
            .fiatSymbol(sellOrder.getFiatSymbol())
            .cryptoSymbol(sellOrder.getCryptoSymbol())
            .build();
    }
}
