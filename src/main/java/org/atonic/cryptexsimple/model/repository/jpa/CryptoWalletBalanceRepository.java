package org.atonic.cryptexsimple.model.repository.jpa;

import org.atonic.cryptexsimple.model.entity.jpa.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.jpa.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.jpa.CryptoWalletBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoWalletBalanceRepository extends JpaRepository<CryptoWalletBalance, Long> {
    Optional<CryptoWalletBalance> findById(CryptoWalletBalanceId cryptoWalletBalanceId);

    List<CryptoWalletBalance> findAllByCryptoWallet(CryptoWallet cryptoWallet);
}
