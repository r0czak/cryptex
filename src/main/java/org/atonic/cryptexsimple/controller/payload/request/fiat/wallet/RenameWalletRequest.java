package org.atonic.cryptexsimple.controller.payload.request.fiat.wallet;

import lombok.Data;

@Data
public class RenameWalletRequest {
    Long walletId;
    String newName;
}
