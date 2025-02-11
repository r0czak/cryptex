package org.atonic.cryptexsimple.controller.utils;

import org.atonic.cryptexsimple.controller.payload.request.orderbook.PlaceOrderRequest;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

import java.math.BigDecimal;

public class OrderbookControllerUtils {
    public static boolean isOrderValid(PlaceOrderRequest request) {
        return request.getAmount().compareTo(BigDecimal.ZERO) > 0
            && request.getPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    public static TradeOrderPOJO prepareTradeOrderPOJO(PlaceOrderRequest request, Long userId) {
        return TradeOrderPOJO.builder()
            .type(request.getType())
            .amount(request.getAmount().toString())
            .price(request.getPrice().toString())
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .userId(userId.toString())
            .cryptoWalletId(request.getCryptoWalletId().toString())
            .cryptoSymbol(request.getCryptoSymbol())
            .fiatWalletId(request.getFiatWalletId().toString())
            .fiatSymbol(request.getFiatSymbol())
            .build();
    }
}
