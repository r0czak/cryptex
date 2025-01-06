package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FIATWalletRepository extends JpaRepository<FIATWallet, Long> {
    List<FIATWallet> findAllByUser(User user);
}
