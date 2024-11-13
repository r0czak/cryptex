package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWallet, Long> {
}
