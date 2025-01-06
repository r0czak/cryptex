package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderType;

import java.math.BigDecimal;

@Data
public class PlaceOrderRequest {
    private OrderType type;
    private CryptoSymbol symbol;
    private BigDecimal amount;
    private BigDecimal price;
    private Long cryptoWalletId;
    private Long fiatWalletId;
}
