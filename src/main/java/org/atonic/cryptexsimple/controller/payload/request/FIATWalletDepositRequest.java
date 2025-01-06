package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

import java.math.BigDecimal;

@Data
public class FIATWalletDepositRequest {
    private Long fiatWalletId;
    private FIATSymbol symbol;
    private BigDecimal amount;
}
