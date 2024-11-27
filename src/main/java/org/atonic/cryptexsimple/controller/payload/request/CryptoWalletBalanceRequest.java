package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoWalletBalanceRequest {
    private Long cryptoWalletId;
    private String symbol;
    private BigDecimal amount;
}
