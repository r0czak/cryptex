package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.CryptoCurrencyPriceDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.time.LocalDateTime;

public interface PriceService {
    CryptoCurrencyPriceDTO getCurrentPrices(CryptoSymbol cryptocurrency, LocalDateTime calculationStartTime, LocalDateTime calculationEndTime);
}
