package org.atonic.cryptexsimple.controller.payload.request.crypto.wallet;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;

@Data
public class CryptoWalletDepositRequest {
    private Long cryptoWalletId;
    private CryptoSymbol symbol;
    private BigDecimal amount;
}
