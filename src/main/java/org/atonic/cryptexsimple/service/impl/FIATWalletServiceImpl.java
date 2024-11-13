package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.repository.FIATWalletRepository;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class FIATWalletServiceImpl implements FIATWalletService {
    private final FIATWalletRepository fiatWalletRepository;

    @Override
    public FIATWallet getFIATWallet(User user) {
        return fiatWalletRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    @Transactional
    public void updateBalance(User user, BigDecimal amount) {
        FIATWallet fiatWallet = getFIATWallet(user);

        fiatWallet.setBalance(fiatWallet.getBalance().add(amount));
        fiatWalletRepository.save(fiatWallet);
    }
}
