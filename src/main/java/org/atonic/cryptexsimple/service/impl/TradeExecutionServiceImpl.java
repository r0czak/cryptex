package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.exception.trade.TradeExecutionException;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.atonic.cryptexsimple.service.OrderbookService;
import org.atonic.cryptexsimple.service.TradeExecutionService;
import org.atonic.cryptexsimple.service.TradeService;
import org.atonic.cryptexsimple.service.VWAPService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class TradeExecutionServiceImpl implements TradeExecutionService {
    private final TradeService tradeService;
    private final OrderbookService orderbookService;
    private final VWAPService vwapService;

    @Transactional
    @Override
    public void executeTradeWithDistributedTransaction(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) throws TradeExecutionException {
        log.info("Executing trade with distributed transaction");
        try {
            TradePOJO trade = prepareTradePOJO(sellOrder, buyOrder);
            tradeService.executeTrade(trade);
            updateOrderbookTradeOrders(sellOrder, buyOrder);
            vwapService.handleNewTrade(trade);
            log.info("Trade executed successfully");
        } catch (Exception e) {
            log.error("Error executing trade: {}", e.getMessage());
            throw new TradeExecutionException("Error while executing trade", e);
        }
    }

    private void updateOrderbookTradeOrders(TradeOrderPOJO sellOrder, TradeOrderPOJO buyOrder) {
        BigDecimal sellAmount = new BigDecimal(sellOrder.getAmount());
        BigDecimal buyAmount = new BigDecimal(buyOrder.getAmount());
        BigDecimal tradeAmount = sellAmount.min(buyAmount);

        sellOrder.setAmount(sellAmount.subtract(tradeAmount).toString());
        buyOrder.setAmount(buyAmount.subtract(tradeAmount).toString());

        if (new BigDecimal(sellOrder.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
            orderbookService.removeTradeOrder(sellOrder.getId());
        } else {
            orderbookService.updateTradeOrder(sellOrder.getId(), sellOrder.getAmount(), sellOrder.getPrice());
        }

        if (new BigDecimal(buyOrder.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
            orderbookService.removeTradeOrder(buyOrder.getId());
        } else {
            orderbookService.updateTradeOrder(buyOrder.getId(), buyOrder.getAmount(), buyOrder.getPrice());
        }
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
