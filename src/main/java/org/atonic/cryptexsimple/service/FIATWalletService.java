package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.entity.FIATWalletBalance;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FIATWalletService {
    Optional<FIATWalletDTO> getFIATWallet(Long fiatWalletId);

    List<FIATWallet> getUserFIATWallets(User user);

    FIATWalletBalance getBalance(Long fiatWalletId, FIATSymbol symbol);

    void updateBalance(Long fiatWalletId, FIATSymbol symbol, BigDecimal amount);
}
