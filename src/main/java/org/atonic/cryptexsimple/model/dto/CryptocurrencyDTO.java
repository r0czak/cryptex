package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

@Data
@Builder
public class CryptocurrencyDTO {
    private Long id;
    private CryptoSymbol cryptoSymbol;
    private String cryptoName;
}
