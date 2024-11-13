package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.FIATWalletRepository;
import org.atonic.cryptexsimple.model.repository.UserRepository;
import org.atonic.cryptexsimple.model.repository.CryptoWalletRepository;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CryptoWalletRepository cryptoWalletRepository;
    private final FIATWalletRepository fiatWalletRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;

    @Override
    public User registerUser(User user) {
        User savedUser = userRepository.save(user);

        CryptoWallet cryptoWallet = new CryptoWallet();
        cryptoWallet.setUser(savedUser);

        // Initialize balances for all cryptocurrencies
        List<Cryptocurrency> cryptocurrencies = cryptocurrencyRepository.findAll();
        for (Cryptocurrency crypto : cryptocurrencies) {
            CryptoWalletBalance balance = new CryptoWalletBalance();
            balance.setCryptoWallet(cryptoWallet);
            balance.setCryptocurrency(crypto);
            balance.setBalance(BigDecimal.ZERO);
            cryptoWallet.getBalances().add(balance);
        }

        cryptoWalletRepository.save(cryptoWallet);

        FIATWallet fiatWallet = new FIATWallet();
        fiatWallet.setUser(savedUser);
        fiatWallet.setBalance(BigDecimal.ZERO);

        fiatWalletRepository.save(fiatWallet);

        return savedUser;
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

}
