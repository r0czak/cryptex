package org.atonic.cryptexsimple.controller.payload.response.crypto.wallet;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;

import java.util.List;

@Data
@Builder
public class UserCryptoWalletBalanceResponse {
    @NonNull
    String auth0UserId;
    @NonNull
    List<CryptoWalletDTO> wallets;
}
