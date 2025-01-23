package org.atonic.cryptexsimple.component;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.jpa.VWAPHistory;
import org.atonic.cryptexsimple.model.entity.redis.VWAPPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.atonic.cryptexsimple.model.pojo.TradePOJO;
import org.atonic.cryptexsimple.model.repository.jpa.VWAPHistoryRepository;
import org.atonic.cryptexsimple.model.repository.redis.RedisVWAPRepository;
import org.springframework.data.keyvalue.core.UncategorizedKeyValueException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Slf4j
@AllArgsConstructor
public class PersistentVWAPTracker {
    private final RedisVWAPRepository redisVWAPRepository;
    private final VWAPHistoryRepository vwapHistoryRepository;

    @Transactional
    public void snapshotVWAP(CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol) {
        Arrays.stream(TimeInterval.values())
            .forEachOrdered(timeInterval -> updateVWAPHistory(cryptoSymbol, fiatSymbol, timeInterval));
    }

    @Transactional
    public void handleTrade(TradePOJO trade) {
        Arrays.stream(TimeInterval.values())
            .forEachOrdered(timeInterval -> updateRedisVWAP(trade, timeInterval));
    }

    private void updateVWAPHistory(CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol, TimeInterval timeInterval) {
        String key = buildKey(cryptoSymbol, fiatSymbol, timeInterval);
        LocalDateTime timestamp = LocalDateTime.now();

        try {
            VWAPPOJO vwap = redisVWAPRepository.findById(key)
                .orElse(createVWAP(cryptoSymbol, fiatSymbol, timestamp, timeInterval));

            if (shouldResetAccumulator(vwap, timestamp, timeInterval)) {
                saveToHistory(vwap);
                vwap = createVWAP(cryptoSymbol, fiatSymbol, timestamp, timeInterval);
            }

            redisVWAPRepository.save(vwap);
        } catch (UncategorizedKeyValueException e) {
            log.error("Failed to update VWAP history for key: {}. Error: {}", key, e.getMessage(), e);
            throw e;
        }

    }

    private void updateRedisVWAP(TradePOJO trade, TimeInterval timeInterval) {
        String key = buildKey(trade.getCryptoSymbol(), trade.getFiatSymbol(), timeInterval);
        LocalDateTime timestamp = LocalDateTime.now();

        try {
            VWAPPOJO vwap = redisVWAPRepository.findById(key)
                .orElse(createVWAP(trade.getCryptoSymbol(), trade.getFiatSymbol(), timestamp, timeInterval));

            if (shouldResetAccumulator(vwap, timestamp, timeInterval)) {
                saveToHistory(vwap);
                vwap = createVWAP(trade.getCryptoSymbol(), trade.getFiatSymbol(), timestamp, timeInterval);
            }

            vwap.setSumPriceVolume(vwap.getSumPriceVolume().add(trade.getPrice().multiply(trade.getAmount())));
            vwap.setTotalVolume(vwap.getTotalVolume().add(trade.getAmount()));
            vwap.setCurrentVWAP(vwap.getSumPriceVolume().divide(vwap.getTotalVolume(), 8, RoundingMode.HALF_UP));
            vwap.setLastUpdated(timestamp);
            vwap.setOpenPrice(vwap.getOpenPrice().compareTo(BigDecimal.ZERO) == 0 ? trade.getPrice() : vwap.getOpenPrice());
            vwap.setClosePrice(trade.getPrice());
            vwap.setHighPrice(vwap.getHighPrice().max(trade.getPrice()));
            vwap.setLowPrice(vwap.getLowPrice().min(trade.getPrice()));

            redisVWAPRepository.save(vwap);
        } catch (UncategorizedKeyValueException e) {
            log.error("Failed to update VWAP history for key: {}. Error: {}", key, e.getMessage(), e);
            throw e;
        }
    }

    private void saveToHistory(VWAPPOJO vwap) {
        VWAPHistory vwapHistory = new VWAPHistory();
        vwapHistory.setCryptoSymbol(vwap.getCryptoSymbol());
        vwapHistory.setFiatSymbol(vwap.getFiatSymbol());
        vwapHistory.setTimestamp(vwap.getLastUpdated());
        vwapHistory.setTradingDate(vwap.getTradingDate());
        vwapHistory.setTimeInterval(vwap.getTimeInterval());
        vwapHistory.setVwap(vwap.getCurrentVWAP());
        vwapHistory.setTotalVolume(vwap.getTotalVolume());
        vwapHistory.setSumPriceVolume(vwap.getSumPriceVolume());
        vwapHistory.setOpenPrice(vwap.getOpenPrice());
        vwapHistory.setClosePrice(vwap.getClosePrice());
        vwapHistory.setHighPrice(vwap.getHighPrice());
        vwapHistory.setLowPrice(vwap.getLowPrice());

        vwapHistoryRepository.save(vwapHistory);
    }

    private VWAPPOJO createVWAP(CryptoSymbol cryptoSymbol,
                                FIATSymbol fiatSymbol,
                                LocalDateTime timestamp,
                                TimeInterval timeInterval) {
        return new VWAPPOJO(
            buildKey(cryptoSymbol, fiatSymbol, timeInterval),
            cryptoSymbol,
            fiatSymbol,
            timestamp,
            timestamp.toLocalDate(),
            timeInterval,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }

    private boolean shouldResetAccumulator(VWAPPOJO vwap, LocalDateTime timestamp, TimeInterval timeInterval) {
        return calculateLocalDateTimeInterval(vwap.getLastUpdated(), timeInterval) != calculateLocalDateTimeInterval(timestamp, timeInterval);
    }

    private String buildKey(CryptoSymbol cryptoSymbol, FIATSymbol fiatSymbol, TimeInterval timeInterval) {
        return String.format("vwap:%s:%s:%s", cryptoSymbol, fiatSymbol, timeInterval);
    }

    private int calculateLocalDateTimeInterval(LocalDateTime timestamp, TimeInterval timeInterval) {
        switch (timeInterval) {
            case ONE_MINUTE -> {
                return timestamp.getMinute();
            }
            case FIVE_MINUTES -> {
                return BigDecimal.valueOf(timestamp.getMinute()).divide(BigDecimal.valueOf(5), RoundingMode.DOWN).intValue();
            }
            case FIFTEEN_MINUTES -> {
                return BigDecimal.valueOf(timestamp.getMinute()).divide(BigDecimal.valueOf(15), RoundingMode.DOWN).intValue();
            }
            case THIRTY_MINUTES -> {
                return BigDecimal.valueOf(timestamp.getMinute()).divide(BigDecimal.valueOf(30), RoundingMode.DOWN).intValue();
            }
            case ONE_HOUR -> {
                return timestamp.getHour();
            }
            case FOUR_HOURS -> {
                return BigDecimal.valueOf(timestamp.getHour()).divide(BigDecimal.valueOf(4), RoundingMode.DOWN).intValue();
            }
            case ONE_DAY -> {
                return timestamp.getDayOfMonth();
            }
            default -> {
                throw new IllegalArgumentException("Invalid interval");
            }
        }
    }
}
