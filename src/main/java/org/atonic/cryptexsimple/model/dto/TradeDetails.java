package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;

@Data
@Builder
public class TradeDetails {
    private final TradePOJO tradePOJO;
    private final User seller;
    private final User buyer;
    private final FIATWallet sellerFIATWallet;
    private final CryptoWallet sellerCryptoWallet;
    private final FIATWallet buyerFIATWallet;
    private final CryptoWallet buyerCryptoWallet;
    private final FIATCurrency tradeFIATCurrency;
    private final Cryptocurrency tradeCryptocurrency;
}
