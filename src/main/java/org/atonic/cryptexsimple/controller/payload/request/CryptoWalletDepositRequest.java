package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoWalletDepositRequest {
    private Long cryptoWalletId;
    private String symbol;
    private BigDecimal amount;
}
