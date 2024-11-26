package org.atonic.cryptexsimple.controller.payload.response.fiat.wallet;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class UserFIATWalletBalanceResponse {
    @NonNull
    String auth0UserId;
    @NonNull
    Long FIATWalletId;
    @NonNull
    BigDecimal amount;
}
