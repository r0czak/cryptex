package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "vwap_history")
public class VWAPHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CryptoSymbol cryptoSymbol;
    @Enumerated(EnumType.STRING)
    private FIATSymbol fiatSymbol;

    private LocalDateTime timestamp;
    private LocalDate tradingDate;
    @Enumerated(EnumType.STRING)
    private TimeInterval timeInterval;

    private BigDecimal vwap;

    @Column(precision = 13, scale = 2)
    private BigDecimal totalVolume;
    private BigDecimal sumPriceVolume;

    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
}
