package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWallet;
import org.atonic.cryptexsimple.model.entity.jpa.FIATWalletBalance;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FIATWalletService {
    Optional<FIATWalletDTO> getFIATWalletDTO(Long fiatWalletId);

    Optional<FIATWallet> getFIATWallet(Long fiatWalletId);

    List<FIATWallet> getUserFIATWallets(User user);

    FIATWalletBalance getBalance(Long fiatWalletId, FIATSymbol symbol);

    void updateBalance(Long fiatWalletId, FIATSymbol symbol, BigDecimal amount);
}
