package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
}
