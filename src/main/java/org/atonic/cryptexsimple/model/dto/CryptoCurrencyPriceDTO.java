package org.atonic.cryptexsimple.model.dto;

import lombok.Builder;
import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CryptoCurrencyPriceDTO {
    Long cryptoId;
    CryptoSymbol symbol;
    BigDecimal calculatedPrice;
    LocalDateTime calculationStartTime;
    LocalDateTime calculationEndTime;
}
