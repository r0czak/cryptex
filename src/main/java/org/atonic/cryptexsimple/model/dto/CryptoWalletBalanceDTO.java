package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoWalletBalanceDTO {
    @NonNull
    String cryptocurrencyName;
    @NonNull
    String cryptocurrencySymbol;
    @NonNull
    BigDecimal balance;
}
