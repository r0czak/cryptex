package org.atonic.cryptexsimple.controller.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;

import java.util.List;

@Data
@Builder
public class UserCryptoWalletBalanceResponse {
    @NonNull
    String auth0UserId;
    @NonNull
    Long cryptoWalletId;
    @NonNull
    List<CryptoWalletBalanceDTO> balances;
}
