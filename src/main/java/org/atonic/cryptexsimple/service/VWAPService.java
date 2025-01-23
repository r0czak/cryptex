package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VWAPService {
    void snapshotVWAP(CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol);

    void handleNewTrade(TradePOJO trade);

    Optional<VWAPHistoryDTO> getIntervalVWAP(CryptoSymbol cryptoSymbol,
                                             FIATSymbol fiatSymbol,
                                             TimeInterval interval);

    Page<VWAPHistoryDTO> getHistoricalVWAP(CryptoSymbol cryptoSymbol,
                                           FIATSymbol fiatSymbol,
                                           LocalDateTime startDateTime,
                                           LocalDateTime endDateTime,
                                           TimeInterval interval,
                                           Pageable pageable);
}
