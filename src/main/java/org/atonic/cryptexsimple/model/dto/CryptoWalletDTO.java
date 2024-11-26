package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CryptoWalletDTO {
    String walletName;
    Long cryptoWalletId;
    List<CryptoWalletBalanceDTO> balances;
}
