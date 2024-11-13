package org.atonic.cryptexsimple.service.impl;

import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.repository.FIATWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FIATWalletServiceImplTest {

    @Mock
    private FIATWalletRepository fiatWalletRepository;

    @InjectMocks
    private FIATWalletServiceImpl fiatWalletService;

    private User user;
    private FIATWallet fiatWallet;

    @BeforeEach
    public void setUp() {
        user = new User("testuser", "testuser@example.com", "password");
        user.setId(1L);

        fiatWallet = new FIATWallet();
        fiatWallet.setId(user.getId());
        fiatWallet.setUser(user);
        fiatWallet.setBalance(BigDecimal.valueOf(1000.0));
    }

    @Test
    void testGetFIATWallet_WalletExists() {
        // Arrange
        when(fiatWalletRepository.findById(user.getId())).thenReturn(Optional.of(fiatWallet));

        // Act
        FIATWallet result = fiatWalletService.getFIATWallet(user);

        // Assert
        assertNotNull(result);
        assertEquals(fiatWallet, result);
        verify(fiatWalletRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGetFIATWallet_WalletNotFound() {
        // Arrange
        when(fiatWalletRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fiatWalletService.getFIATWallet(user);
        });
        assertEquals("Wallet not found", exception.getMessage());
        verify(fiatWalletRepository, times(1)).findById(user.getId());
    }

    @Test
    void testUpdateBalance_Success() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500.0);
        when(fiatWalletRepository.findById(user.getId())).thenReturn(Optional.of(fiatWallet));

        // Act
        fiatWalletService.updateBalance(user, amount);

        // Assert
        assertEquals(BigDecimal.valueOf(1500.0), fiatWallet.getBalance());
        verify(fiatWalletRepository, times(1)).findById(user.getId());
        verify(fiatWalletRepository, times(1)).save(fiatWallet);
    }

    @Test
    void testUpdateBalance_WalletNotFound() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500.0);
        when(fiatWalletRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fiatWalletService.updateBalance(user, amount);
        });
        assertEquals("Wallet not found", exception.getMessage());
        verify(fiatWalletRepository, times(1)).findById(user.getId());
        verify(fiatWalletRepository, never()).save(any());
    }
}
