package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.FIATCurrency;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FIATCurrencyRepository extends JpaRepository<FIATCurrency, Long> {
    Optional<FIATCurrency> findBySymbol(FIATSymbol symbol);
}
