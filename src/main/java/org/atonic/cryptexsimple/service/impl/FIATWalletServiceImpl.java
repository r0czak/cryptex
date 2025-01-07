package org.atonic.cryptexsimple.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.mapper.FIATWalletBalanceMapper;
import org.atonic.cryptexsimple.mapper.FIATWalletMapper;
import org.atonic.cryptexsimple.model.dto.FIATWalletBalanceDTO;
import org.atonic.cryptexsimple.model.dto.FIATWalletDTO;
import org.atonic.cryptexsimple.model.entity.jpa.*;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;
import org.atonic.cryptexsimple.model.repository.jpa.FIATCurrencyRepository;
import org.atonic.cryptexsimple.model.repository.jpa.FIATWalletBalanceRepository;
import org.atonic.cryptexsimple.model.repository.jpa.FIATWalletRepository;
import org.atonic.cryptexsimple.service.FIATWalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FIATWalletServiceImpl implements FIATWalletService {
    private final FIATWalletRepository fiatWalletRepository;
    private final FIATCurrencyRepository fiarCurrencyRepository;
    private final FIATWalletBalanceRepository fiatWalletBalanceRepository;

    private final FIATWalletMapper fiatWalletMapper;
    private final FIATWalletBalanceMapper fiatWalletBalanceMapper;

    @Override
    public Optional<FIATWalletDTO> getFIATWallet(Long fiatWalletId) {
        Optional<FIATWallet> fiatWallet = fiatWalletRepository.findById(fiatWalletId);
        if (fiatWallet.isEmpty()) {
            return Optional.empty();
        }

        List<FIATWalletBalanceDTO> balances = fiatWalletBalanceRepository.findAllByFiatWallet(fiatWallet.get()).stream()
            .map(fiatWalletBalanceMapper::toFIATWalletBalanceDTO).toList();

        return Optional.of(fiatWalletMapper.toFIATWalletDTO(fiatWallet.get(), balances));
    }

    @Override
    public List<FIATWallet> getUserFIATWallets(User user) {
        return fiatWalletRepository.findAllByUser(user);
    }

    @Override
    public FIATWalletBalance getBalance(Long fiatWalletId, FIATSymbol symbol) {
        FIATWallet fiatWallet = fiatWalletRepository.findById(fiatWalletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        FIATCurrency fiatCurrency = fiarCurrencyRepository.findBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException("FIAT currency not found"));

        FIATWalletBalanceId id = new FIATWalletBalanceId();
        id.setFiatWalletId(fiatWallet.getId());
        id.setFiatCurrencyId(fiatCurrency.getId());

        return fiatWalletBalanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Balance not found"));
    }


    @Override
    @Transactional
    public void updateBalance(Long fiatWalletId, FIATSymbol symbol, BigDecimal amount) {
        FIATWalletBalance balance = getBalance(fiatWalletId, symbol);
        balance.setBalance(balance.getBalance().add(amount));
        fiatWalletBalanceRepository.save(balance);
    }
}
