package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.CryptocurrencyPriceDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.time.LocalDateTime;

public interface PriceService {
    CryptocurrencyPriceDTO getCurrentPrices(CryptoSymbol cryptocurrency, LocalDateTime calculationStartTime, LocalDateTime calculationEndTime);
}
