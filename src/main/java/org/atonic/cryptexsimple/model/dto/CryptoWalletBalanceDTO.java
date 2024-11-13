package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoWalletBalanceDTO {
    @NonNull
    String cryptocurrency;
    @NonNull
    BigDecimal balance;
}
