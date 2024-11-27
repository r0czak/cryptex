package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CryptoWalletService {
    Optional<CryptoWalletDTO> getCryptoWallet(User user, Long cryptoWalletId);

    List<CryptoWallet> getCryptoWallets(User user);

    Optional<CryptoWallet> createNewWallet(User user, String walletName);

    CryptoWalletBalance getBalance(User user, Long cryptoWalletId, CryptoSymbol symbol);

    void updateBalance(User user, Long cryptoWalletId, CryptoSymbol symbol, BigDecimal amount);
}
