package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findAllByCryptocurrencyAndTimestampBetween(Cryptocurrency crypto, LocalDateTime from, LocalDateTime to);

    List<Trade> findTop3ByCryptocurrencyOrderByTimestamp(Cryptocurrency crypto);
}
