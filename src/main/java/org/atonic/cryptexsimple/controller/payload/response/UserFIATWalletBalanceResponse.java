package org.atonic.cryptexsimple.controller.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class UserFIATWalletBalanceResponse {
    @NonNull
    String userName;
    @NonNull
    Long FIATWalletId;
    @NonNull
    BigDecimal amount;
}
