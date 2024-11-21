package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.repository.CryptoWalletRepository;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.FIATWalletRepository;
import org.atonic.cryptexsimple.model.repository.UserRepository;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public User getOrRegisterUser(Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        Optional<User> user = userRepository.findByAuth0UserId(auth0UserId);

        return user.orElseGet(() -> registerUser(auth0UserId, email));
    }

    @Override
    public User registerUser(String auth0UserId, String email) {
        User newUser = new User();
        newUser.setAuth0UserId(auth0UserId);
        newUser.setEmail(email);
        newUser.setDeleted(false);

        CryptoWallet cryptoWallet = new CryptoWallet();
        cryptoWallet.setUser(newUser);

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
        fiatWallet.setUser(newUser);
        fiatWallet.setBalance(BigDecimal.ZERO);

        fiatWalletRepository.save(fiatWallet);

        return newUser;
    }

    @Override
    public Optional<User> findUserByAuth0UserId(String auth0UserId) {
        return userRepository.findByAuth0UserId(auth0UserId);
    }

}
