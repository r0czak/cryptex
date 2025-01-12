package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.CryptoWalletBalanceMapper;
import org.atonic.cryptexsimple.mapper.CryptoWalletMapper;
import org.atonic.cryptexsimple.model.dto.CryptoWalletBalanceDTO;
import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptoWalletBalanceRepository;
import org.atonic.cryptexsimple.model.repository.jpa.CryptoWalletRepository;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
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
    public Optional<CryptoWalletDTO> getCryptoWalletDTO(Long cryptoWalletId) {
        Optional<CryptoWallet> cryptoWalletOptional = cryptoWalletRepository.findById(cryptoWalletId);
        if (cryptoWalletOptional.isEmpty()) {
            return Optional.empty();
        }

        List<CryptoWalletBalanceDTO> balances = cryptoWalletBalanceRepository.findAllByCryptoWallet(cryptoWalletOptional.get()).stream()
            .map(cryptoWalletBalanceMapper::toCryptoWalletBalanceDTO)
            .toList();


        return Optional.of(cryptoWalletMapper.toCryptoWalletDTO(cryptoWalletOptional.get(), balances));

    }

    @Override
    public Optional<CryptoWallet> getCryptoWallet(Long cryptoWalletId) {
        return cryptoWalletRepository.findById(cryptoWalletId);
    }

    @Override
    public List<CryptoWallet> getUserCryptoWallets(User user) {
        return cryptoWalletRepository.findAllByUserOrderById(user);
    }

    @Override
    public CryptoWalletBalance getBalance(Long cryptoWalletId, CryptoSymbol symbol) {
        CryptoWallet cryptoWallet = cryptoWalletRepository.findById(cryptoWalletId)
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
    public void updateBalance(Long cryptoWalletId, CryptoSymbol symbol, BigDecimal amount) {
        CryptoWalletBalance balance = getBalance(cryptoWalletId, symbol);
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

    @Override
    public void renameWallet(Long cryptoWalletId, String walletName) {
        Optional<CryptoWallet> cryptoWalletOptional = cryptoWalletRepository.findById(cryptoWalletId);
        if (cryptoWalletOptional.isPresent()) {
            CryptoWallet cryptoWallet = cryptoWalletOptional.get();
            cryptoWallet.setWalletName(walletName);
            cryptoWalletRepository.save(cryptoWallet);
        }
    }

    @Override
    @Transactional
    public Optional<CryptoWallet> transferAllFunds(Long sourceWalletId, Long targetWalletId) {
        if (sourceWalletId.equals(targetWalletId)) {
            return Optional.empty();
        }

        Optional<CryptoWallet> sourceCryptoWallet = cryptoWalletRepository.findById(sourceWalletId);
        Optional<CryptoWallet> targetCryptoWallet = cryptoWalletRepository.findById(targetWalletId);

        if (sourceCryptoWallet.isEmpty() || targetCryptoWallet.isEmpty()) {
            return Optional.empty();
        }

        CryptoWallet sourceWallet = sourceCryptoWallet.get();
        CryptoWallet targetWallet = targetCryptoWallet.get();

        sourceWallet.getBalances().forEach(balance -> {
            CryptoWalletBalanceId targetBalanceId = new CryptoWalletBalanceId();
            targetBalanceId.setCryptoWalletId(targetWalletId);
            targetBalanceId.setCryptocurrencyId(balance.getCryptocurrency().getId());

            cryptoWalletBalanceRepository.findById(targetBalanceId).ifPresent(targetBalance -> {
                targetBalance.setBalance(targetBalance.getBalance().add(balance.getBalance()));
                cryptoWalletBalanceRepository.save(targetBalance);
            });
        });

        return Optional.of(targetWallet);
    }

    @Override
    @Transactional
    public void deleteCryptoWallet(Long cryptoWalletId) {
        Optional<CryptoWallet> cryptoWallet = cryptoWalletRepository.findById(cryptoWalletId);

        if (cryptoWallet.isPresent()) {
            cryptoWalletRepository.deleteById(cryptoWalletId);
        }
    }

}
