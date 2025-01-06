package org.atonic.cryptexsimple.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FIATWalletDTO {
    Long fiatWalletId;
    List<FIATWalletBalanceDTO> balances;
}
