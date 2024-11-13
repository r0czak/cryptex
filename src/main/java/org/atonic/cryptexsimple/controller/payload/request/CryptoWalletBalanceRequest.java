package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;

@Data
public class CryptoWalletBalanceRequest {
    private CryptoSymbol symbol;
    private BigDecimal amount;
}
