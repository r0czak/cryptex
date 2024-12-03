package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

@Data
public class TransferFundsRequest {
    Long sourceWalletId;
    Long targetWalletId;
}
