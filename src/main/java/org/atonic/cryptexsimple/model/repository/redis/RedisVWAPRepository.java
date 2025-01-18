package org.atonic.cryptexsimple.model.repository.redis;

import org.atonic.cryptexsimple.model.entity.redis.VWAPPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.enums.TimeInterval;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RedisVWAPRepository extends CrudRepository<VWAPPOJO, String> {
    List<VWAPPOJO> findByCryptoSymbolAndFiatSymbolAndTimeIntervalAndTradingDateBetween(
        CryptoSymbol cryptoSymbol,
        FIATSymbol fiatSymbol,
        TimeInterval timeInterval,
        LocalDate start,
        LocalDate end
    );

    Optional<VWAPPOJO> findFirstByCryptoSymbolAndFiatSymbolAndTimeInterval(
        CryptoSymbol cryptoSymbol,
        FIATSymbol fiatSymbol,
        TimeInterval timeInterval
    );
}
