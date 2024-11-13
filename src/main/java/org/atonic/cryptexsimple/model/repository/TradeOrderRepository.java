package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.entity.TradeOrder;
import org.atonic.cryptexsimple.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    List<TradeOrder> findByStatus(OrderStatus status);
    List<TradeOrder> findByStatusAndCryptocurrency(OrderStatus status, Cryptocurrency cryptocurrency);
}
