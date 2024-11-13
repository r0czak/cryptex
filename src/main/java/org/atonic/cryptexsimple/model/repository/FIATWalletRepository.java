package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FIATWalletRepository extends JpaRepository<FIATWallet, Long> {
}
