package org.atonic.cryptexsimple.service.impl;

import org.atonic.cryptexsimple.model.dto.CryptoWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.CryptoWalletBalanceRepository;
import org.atonic.cryptexsimple.model.repository.jpa.CryptoWalletRepository;
import org.atonic.cryptexsimple.model.repository.jpa.CryptocurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoWalletServiceImplTest {

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private CryptocurrencyRepository cryptocurrencyRepository;

    @Mock
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @InjectMocks
    private CryptoWalletServiceImpl cryptoWalletService;

    private User user;
    private CryptoWallet wallet;
    private Cryptocurrency crypto;
    private CryptoWalletBalance balance;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "testuser", "test@example.com", true);

        wallet = new CryptoWallet();
        wallet.setId(user.getId());
        wallet.setUser(user);

        crypto = new Cryptocurrency();
        crypto.setId(1L);
        crypto.setSymbol(CryptoSymbol.BTC);

        balance = new CryptoWalletBalance();
        balance.setCryptoWallet(wallet);
        balance.setCryptocurrency(crypto);
        balance.setBalance(new BigDecimal("100.0"));
    }

    @Test
    void test_retrieve_crypto_wallet_for_valid_user() {
        // Arrange
        when(cryptoWalletRepository.findById(user.getId())).thenReturn(Optional.of(wallet));

        // Act
        Optional<CryptoWalletDTO> actualWallet = cryptoWalletService.getCryptoWallet(user, 1L);

        // Assert
        assertEquals(true, actualWallet.isPresent());
        assertEquals(wallet, actualWallet);
    }

    @Test
    void test_retrieve_balance_for_specific_cryptocurrency() {
        // Arrange
        when(cryptoWalletRepository.findById(user.getId())).thenReturn(Optional.of(wallet));
        when(cryptocurrencyRepository.findBySymbol(CryptoSymbol.BTC)).thenReturn(Optional.of(crypto));
        when(cryptoWalletBalanceRepository.findById(any(CryptoWalletBalanceId.class))).thenReturn(Optional.of(balance));

        // Act
        CryptoWalletBalance result = cryptoWalletService.getBalance(user, CryptoSymbol.BTC);

        // Assert
        assertNotNull(result);
        assertEquals(balance.getBalance(), result.getBalance());
    }

    @Test
    void test_successful_balance_update() {
        // Arrange
        BigDecimal amount = new BigDecimal("10.0");

        when(cryptoWalletRepository.findById(user.getId())).thenReturn(Optional.of(wallet));
        when(cryptocurrencyRepository.findBySymbol(CryptoSymbol.BTC)).thenReturn(Optional.of(crypto));
        when(cryptoWalletBalanceRepository.findById(any(CryptoWalletBalanceId.class))).thenReturn(Optional.of(balance));

        // Act
        cryptoWalletService.updateBalance(user, CryptoSymbol.BTC, amount);

        // Assert
        assertEquals(new BigDecimal("110.0"), balance.getBalance());
    }
}
