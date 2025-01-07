package org.atonic.cryptexsimple.model.repository.redis;

import org.atonic.cryptexsimple.model.entity.redis.TradeOrderPOJO;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.enums.OrderStatus;
import org.atonic.cryptexsimple.model.enums.OrderType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisTradeOrderRepository extends CrudRepository<TradeOrderPOJO, String> {
    List<TradeOrderPOJO> findByCryptoSymbolAndTypeOrderByPriceDesc(
        CryptoSymbol symbol,
        OrderType type,
        Pageable pageable);

    List<TradeOrderPOJO> findByCryptoSymbolAndTypeAndStatusOrderByTimestampAsc(
        CryptoSymbol symbol,
        OrderType type,
        OrderStatus status,
        Pageable pageable
    );
}
