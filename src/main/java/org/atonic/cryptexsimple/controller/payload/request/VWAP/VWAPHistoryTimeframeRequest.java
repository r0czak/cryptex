package org.atonic.cryptexsimple.controller.payload.request.VWAP;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;

import java.time.LocalDateTime;

@Data
public class VWAPHistoryTimeframeRequest {
    CryptoSymbol cryptoSymbol;
    FIATSymbol fiatSymbol;
    LocalDateTime startDate;
    LocalDateTime endDate;
    TimeInterval timeInterval;
}
