package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeDTO {
    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime timestamp;

    private Long sellerFIATWalletId;
    private Long sellerCryptoWalletId;

    private Long buyerFIATWalletId;
    private Long buyerCryptoWalletId;

    private FIATSymbol fiatSymbol;
    private CryptoSymbol cryptoSymbol;
}
