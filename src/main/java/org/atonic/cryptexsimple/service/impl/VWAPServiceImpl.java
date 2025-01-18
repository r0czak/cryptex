package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.component.PersistentVWAPTracker;
import org.atonic.cryptexsimple.mapper.VWAPHistoryMapper;
import org.atonic.cryptexsimple.mapper.VWAPPOJOMapper;
import org.atonic.cryptexsimple.model.dto.VWAPHistoryDTO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.atonic.cryptexsimple.model.repository.jpa.VWAPHistoryRepository;
import org.atonic.cryptexsimple.model.repository.redis.RedisVWAPRepository;
import org.atonic.cryptexsimple.service.VWAPService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class VWAPServiceImpl implements VWAPService {
    private final PersistentVWAPTracker vwapTracker;

    private final RedisVWAPRepository vwapRepository;
    private final VWAPHistoryRepository vwapHistoryRepository;

    private final VWAPHistoryMapper vwapHistoryMapper;
    private final VWAPPOJOMapper vwapPOJOMapper;

    @Override
    public void snapshotVWAP(CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol) {
        vwapTracker.snapshotVWAP(cryptoSymbol, fiatSymbol);
    }

    @Override
    public void handleNewTrade(TradePOJO trade) {
        vwapTracker.handleTrade(trade);
    }

    @Override
    public Optional<VWAPHistoryDTO> getIntervalVWAP(CryptoSymbol cryptoSymbol,
                                                    FIATSymbol fiatSymbol,
                                                    TimeInterval timeInterval) {
        String key = String.format("%s:%s:%s", cryptoSymbol, fiatSymbol, timeInterval);

        return vwapRepository.findById(key)
            .or(() -> vwapRepository.findFirstByCryptoSymbolAndFiatSymbolAndTimeInterval(
                cryptoSymbol, fiatSymbol, timeInterval))
            .map(vwapPOJOMapper::toVWAPHistoryDTO);
    }

    @Override
    public Page<VWAPHistoryDTO> getHistoricalVWAP(CryptoSymbol cryptoSymbol,
                                                  FIATSymbol fiatSymbol,
                                                  LocalDateTime startDateTime,
                                                  LocalDateTime endDateTime,
                                                  TimeInterval timeInterval,
                                                  Pageable pageable) {
        return vwapHistoryRepository.findByCryptoSymbolAndFiatSymbolAndTimeIntervalAndTimestampBetweenOrderByTimestamp(
                cryptoSymbol, fiatSymbol, timeInterval, startDateTime, endDateTime, pageable)
            .map(vwapHistoryMapper::toVWAPHistoryDTO);
    }
}
