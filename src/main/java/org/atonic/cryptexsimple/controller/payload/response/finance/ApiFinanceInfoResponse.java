package org.atonic.cryptexsimple.controller.payload.response.finance;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;

import java.util.List;

@Data
@Builder
public class ApiFinanceInfoResponse {
    List<CryptoWalletDTO> cryptoWallets;
    List<FIATWalletDTO> fiatWallets;
}
