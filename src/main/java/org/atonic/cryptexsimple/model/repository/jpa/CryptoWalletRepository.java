package org.atonic.cryptexsimple.model.repository.jpa;

import org.atonic.cryptexsimple.model.entity.jpa.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWallet, Long> {
    List<CryptoWallet> findAllByUserOrderById(User user);

    void deleteByIdAndUser(Long id, User user);
}
