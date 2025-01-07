package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.repository.jpa.*;
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
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final FIATWalletRepository fiatWalletRepository;
    private final FIATCurrencyRepository fiatCurrencyRepository;

    @Override
    public Optional<User> getUser(Jwt jwt) {
        String auth0UserId = jwt.getSubject();

        return userRepository.findByAuth0UserId(auth0UserId);
    }

    @Override
    public User registerUser(Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        Optional<User> user = userRepository.findByAuth0UserId(auth0UserId);

        return user.orElseGet(() -> addUser(auth0UserId, email));
    }

    @Override
    public User addUser(String auth0UserId, String email) {
        User newUser = new User();
        newUser.setAuth0UserId(auth0UserId);
        newUser.setEmail(email);
        newUser.setDeleted(false);

        CryptoWallet cryptoWallet = new CryptoWallet();
        cryptoWallet.setWalletName("Wallet1");
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


        FIATWallet fiatWallet = new FIATWallet();
        fiatWallet.setUser(newUser);

        List<FIATCurrency> fiatCurrencies = fiatCurrencyRepository.findAll();
        for (FIATCurrency fiatCurrency : fiatCurrencies) {
            FIATWalletBalance balance = new FIATWalletBalance();
            balance.setFiatWallet(fiatWallet);
            balance.setFiatCurrency(fiatCurrency);
            balance.setBalance(BigDecimal.ZERO);
            fiatWallet.getBalances().add(balance);
        }

        userRepository.save(newUser);
        fiatWalletRepository.save(fiatWallet);
        cryptoWalletRepository.save(cryptoWallet);

        return newUser;
    }

    @Override
    public Optional<User> findUserByAuth0UserId(String auth0UserId) {
        return userRepository.findByAuth0UserId(auth0UserId);
    }

}
