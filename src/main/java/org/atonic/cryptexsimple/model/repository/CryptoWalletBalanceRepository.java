package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoWalletBalanceRepository extends JpaRepository<CryptoWalletBalance, Long> {
    Optional<CryptoWalletBalance> findById(CryptoWalletBalanceId cryptoWalletBalanceId);
    List<CryptoWalletBalance> findAllByCryptoWallet(CryptoWallet cryptoWallet);
}
