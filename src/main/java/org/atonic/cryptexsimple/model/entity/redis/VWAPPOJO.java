package org.atonic.cryptexsimple.model.entity.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RedisHash("vwap")
@AllArgsConstructor
public class VWAPPOJO {
    @Id
    private String id;

    @Indexed
    private CryptoSymbol cryptoSymbol;
    @Indexed
    private FIATSymbol fiatSymbol;

    private LocalDateTime lastUpdated;
    @Indexed
    private LocalDate tradingDate;
    @Indexed
    private TimeInterval timeInterval;

    private BigDecimal currentVWAP;
    private BigDecimal totalVolume;
    private BigDecimal sumPriceVolume;

    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
}
