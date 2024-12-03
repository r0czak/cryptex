package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

@Data
public class RenameWalletRequest {
    Long walletId;
    String newName;
}
