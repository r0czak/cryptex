package org.atonic.cryptexsimple.controller.utils;

import org.atonic.cryptexsimple.controller.payload.request.orderbook.PlaceOrderRequest;
import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;

public class OrderbookControllerUtils {
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
