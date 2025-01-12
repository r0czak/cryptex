package org.atonic.cryptexsimple.model.pojo;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

import java.math.BigDecimal;

@Data
@Builder
public class TradePOJO {
    private BigDecimal amount;
    private BigDecimal price;

    private Long sellerId;
    private Long sellerFIATWalletId;
    private Long sellerCryptoWalletId;

    private Long buyerId;
    private Long buyerFIATWalletId;
    private Long buyerCryptoWalletId;

    private FIATSymbol fiatSymbol;
    private CryptoSymbol cryptoSymbol;
}
