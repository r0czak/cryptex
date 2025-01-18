package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class VWAPHistoryDTO {
    Long id;
    CryptoSymbol cryptoSymbol;
    FIATSymbol fiatSymbol;

    LocalDateTime timestamp;
    LocalDate tradingDate;
    TimeInterval timeInterval;

    BigDecimal vwap;
    BigDecimal totalVolume;
    BigDecimal sumPriceVolume;

    BigDecimal openPrice;
    BigDecimal closePrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
}
