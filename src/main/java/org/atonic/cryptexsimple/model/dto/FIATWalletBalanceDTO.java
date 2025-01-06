package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FIATWalletBalanceDTO {
    String fiatCurrencyName;
    String fiatCurrencySymbol;
    BigDecimal balance;
}
