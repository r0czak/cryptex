package org.atonic.cryptexsimple.controller.payload.request.crypto.wallet;

import lombok.Data;

@Data
public class TransferFundsRequest {
    Long sourceWalletId;
    Long targetWalletId;
}
