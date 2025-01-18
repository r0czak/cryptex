package org.atonic.cryptexsimple.controller.payload.request.VWAP;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;

@Data
public class VWAPHistoryCurrentRequest {
    CryptoSymbol cryptoSymbol;
    FIATSymbol fiatSymbol;

    TimeInterval timeInterval;
}
