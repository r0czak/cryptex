package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.Cryptocurrency;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, Long> {
    Optional<Cryptocurrency> findBySymbol(CryptoSymbol symbol);
}
