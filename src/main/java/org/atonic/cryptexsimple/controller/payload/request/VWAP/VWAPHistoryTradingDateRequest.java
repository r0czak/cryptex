package org.atonic.cryptexsimple.controller.payload.request.VWAP;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

@Data
public class VWAPHistoryTradingDateRequest {
    CryptoSymbol cryptoSymbol;
    FIATSymbol fiatSymbol;
    String tradingDate;
}
