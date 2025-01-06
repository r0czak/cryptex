package org.atonic.cryptexsimple.controller.payload.response.fiat.wallet;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;

@Data
@Builder
public class UserFIATWalletBalanceResponse {
    @NonNull
    String auth0UserId;
    @NonNull
    FIATWalletDTO wallet;
}
