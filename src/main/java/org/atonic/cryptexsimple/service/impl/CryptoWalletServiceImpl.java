package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.CryptoWalletBalanceRepository;
import org.atonic.cryptexsimple.model.repository.CryptoWalletRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CryptoWalletServiceImpl implements CryptoWalletService {
    private final CryptoWalletRepository cryptoWalletRepository;
    private final CryptoWalletBalanceRepository cryptoWalletBalanceRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;

    @Override
    public CryptoWallet getCryptoWallet(User user) {
        return cryptoWalletRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    public CryptoWalletBalance getBalance(User user, CryptoSymbol symbol) {
        CryptoWallet cryptoWallet = cryptoWalletRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Cryptocurrency crypto = cryptocurrencyRepository.findBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException("Cryptocurrency not found"));

        CryptoWalletBalanceId id = new CryptoWalletBalanceId();
        id.setCryptoWalletId(cryptoWallet.getId());
        id.setCryptocurrencyId(crypto.getId());

        return cryptoWalletBalanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Balance not found"));
    }

    @Override
    @Transactional
    public void updateBalance(User user, CryptoSymbol symbol, BigDecimal amount) {
        CryptoWalletBalance balance = getBalance(user, symbol);
        balance.setBalance(balance.getBalance().add(amount));
        cryptoWalletBalanceRepository.save(balance);
    }
}
