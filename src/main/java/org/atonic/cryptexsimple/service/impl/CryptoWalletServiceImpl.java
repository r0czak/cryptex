package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.CryptoWalletBalanceMapper;
import org.atonic.cryptexsimple.mapper.CryptoWalletMapper;
import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptoWalletBalanceRepository;
import org.atonic.cryptexsimple.model.repository.CryptoWalletRepository;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.service.CryptoWalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CryptoWalletServiceImpl implements CryptoWalletService {
    private final CryptoWalletRepository cryptoWalletRepository;
    private final CryptoWalletBalanceRepository cryptoWalletBalanceRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final CryptoWalletMapper cryptoWalletMapper;
    private final CryptoWalletBalanceMapper cryptoWalletBalanceMapper;

    @Override
    public Optional<CryptoWalletDTO> getCryptoWallet(User user, Long id) {
        Optional<CryptoWallet> cryptoWalletOptional = cryptoWalletRepository.findByIdAndUser(id, user);
        if (cryptoWalletOptional.isEmpty()) {
            return Optional.empty();
        }

        List<CryptoWalletBalanceDTO> balances = cryptoWalletBalanceRepository.findAllByCryptoWallet(cryptoWalletOptional.get()).stream()
            .map(cryptoWalletBalanceMapper::toCryptoWalletBalanceDTO)
            .toList();


        return Optional.of(cryptoWalletMapper.toCryptoWalletDTO(cryptoWalletOptional.get(), balances));

    }

    @Override
    public List<CryptoWallet> getCryptoWallets(User user) {
        return cryptoWalletRepository.findAllByUserOrderById(user);
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

    @Override
    public Optional<CryptoWallet> createNewWallet(User user, String walletName) {
        List<CryptoWallet> wallets = cryptoWalletRepository.findAllByUserOrderById(user);
        if (wallets.size() >= 5) {
            return Optional.empty();
        }

        CryptoWallet newWallet = new CryptoWallet();
        newWallet.setUser(user);
        newWallet.setWalletName(walletName);

        List<Cryptocurrency> cryptocurrencies = cryptocurrencyRepository.findAll();
        for (Cryptocurrency crypto : cryptocurrencies) {
            CryptoWalletBalance balance = new CryptoWalletBalance();
            balance.setCryptoWallet(newWallet);
            balance.setCryptocurrency(crypto);
            balance.setBalance(BigDecimal.ZERO);
            newWallet.getBalances().add(balance);
        }

        try {
            cryptoWalletRepository.save(newWallet);
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(newWallet);
    }
}
