package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.jpa.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CryptoWalletService {
    Optional<CryptoWalletDTO> getCryptoWalletDTO(Long cryptoWalletId);

    Optional<CryptoWallet> getCryptoWallet(Long cryptoWalletId);

    List<CryptoWallet> getUserCryptoWallets(User user);

    Optional<CryptoWallet> createNewWallet(User user, String walletName);

    void renameWallet(Long cryptoWalletId, String walletName);

    Optional<CryptoWallet> transferAllFunds(Long sourceWalletId, Long targetWalletId);

    void deleteCryptoWallet(Long cryptoWalletId);

    CryptoWalletBalance getBalance(Long cryptoWalletId, CryptoSymbol symbol);

    void updateBalance(Long cryptoWalletId, CryptoSymbol symbol, BigDecimal amount, BigDecimal price);
}
