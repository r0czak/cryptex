package org.atonic.cryptexsimple.model.repository.jpa;

import org.atonic.cryptexsimple.model.entity.jpa.VWAPHistory;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VWAPHistoryRepository extends JpaRepository<VWAPHistory, Long> {
    Page<VWAPHistory> findByCryptoSymbolAndFiatSymbolAndTimeIntervalAndTimestampBetweenOrderByTimestamp(
        CryptoSymbol cryptoSymbol,
        FIATSymbol fiatSymbol,
        TimeInterval timeInterval,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable
    );
}
