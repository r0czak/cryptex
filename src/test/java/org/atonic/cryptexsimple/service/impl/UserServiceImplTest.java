package org.atonic.cryptexsimple.service.impl;

import org.atonic.cryptexsimple.model.entity.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;
import org.atonic.cryptexsimple.model.repository.CryptocurrencyRepository;
import org.atonic.cryptexsimple.model.repository.CryptoWalletRepository;
import org.atonic.cryptexsimple.model.repository.FIATWalletRepository;
import org.atonic.cryptexsimple.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private FIATWalletRepository fiatWalletRepository;

    @Mock
    private CryptocurrencyRepository cryptocurrencyRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Cryptocurrency crypto1;
    private Cryptocurrency crypto2;
    private List<Cryptocurrency> cryptocurrencies;

    @BeforeEach
    public void setUp() {
        user = new User("testuser", "testuser@example.com", "password");
        user.setId(1L);

        crypto1 = new Cryptocurrency();
        crypto1.setId(1L);
        crypto1.setSymbol(CryptoSymbol.BTC);

        crypto2 = new Cryptocurrency();
        crypto2.setId(2L);
        crypto2.setSymbol(CryptoSymbol.ETH);

        cryptocurrencies = Arrays.asList(crypto1, crypto2);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);
        when(cryptocurrencyRepository.findAll()).thenReturn(cryptocurrencies);
        when(cryptoWalletRepository.save(any(CryptoWallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fiatWalletRepository.save(any(FIATWallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.registerUser(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals(user, savedUser);

        verify(userRepository, times(1)).save(user);
        verify(cryptocurrencyRepository, times(1)).findAll();
        verify(cryptoWalletRepository, times(1)).save(any(CryptoWallet.class));
        verify(fiatWalletRepository, times(1)).save(any(FIATWallet.class));
    }

    @Test
    void testRegisterUser_CryptoWalletInitialization() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);
        when(cryptocurrencyRepository.findAll()).thenReturn(cryptocurrencies);
        when(cryptoWalletRepository.save(any(CryptoWallet.class))).thenAnswer(invocation -> {
            CryptoWallet wallet = invocation.getArgument(0);
            wallet.setId(1L);
            return wallet;
        });
        when(fiatWalletRepository.save(any(FIATWallet.class))).thenAnswer(invocation -> {
            FIATWallet wallet = invocation.getArgument(0);
            wallet.setId(1L);
            return wallet;
        });

        // Act
        User savedUser = userService.registerUser(user);

        // Assert
        assertNotNull(savedUser);

        verify(cryptoWalletRepository).save(argThat(wallet -> {
            assertEquals(savedUser, wallet.getUser());
            assertEquals(2, wallet.getBalances().size());
            for (CryptoWalletBalance balance : wallet.getBalances()) {
                assertEquals(BigDecimal.ZERO, balance.getBalance());
                assertTrue(cryptocurrencies.contains(balance.getCryptocurrency()));
                assertEquals(wallet, balance.getCryptoWallet());
            }
            return true;
        }));
    }

    @Test
    void testRegisterUser_FIATWalletInitialization() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);
        when(cryptocurrencyRepository.findAll()).thenReturn(cryptocurrencies);
        when(cryptoWalletRepository.save(any(CryptoWallet.class))).thenReturn(new CryptoWallet());
        when(fiatWalletRepository.save(any(FIATWallet.class))).thenAnswer(invocation -> {
            FIATWallet wallet = invocation.getArgument(0);
            wallet.setId(1L);
            return wallet;
        });

        // Act
        User savedUser = userService.registerUser(user);

        // Assert
        assertNotNull(savedUser);

        verify(fiatWalletRepository).save(argThat(wallet -> {
            assertEquals(savedUser, wallet.getUser());
            assertEquals(BigDecimal.ZERO, wallet.getBalance());
            return true;
        }));
    }

    @Test
    void testFindByUserName_UserExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUserName("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUserName_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUserName("unknownuser");

        // Assert
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findByUsername("unknownuser");
    }
}
