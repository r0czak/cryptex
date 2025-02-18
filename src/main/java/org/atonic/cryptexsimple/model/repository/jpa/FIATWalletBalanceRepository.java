package org.atonic.cryptexsimple.model.repository.jpa;

import org.atonic.cryptexsimple.model.entity.jpa.FIATWallet;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWalletBalance;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWalletBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FIATWalletBalanceRepository extends JpaRepository<FIATWalletBalance, Long> {
    Optional<FIATWalletBalance> findById(FIATWalletBalanceId fiatWalletBalanceId);

    List<FIATWalletBalance> findAllByFiatWallet(FIATWallet fiatWallet);
}
