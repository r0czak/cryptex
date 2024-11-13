package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.FIATWallet;
import org.atonic.cryptexsimple.model.entity.User;

import java.math.BigDecimal;

public interface FIATWalletService {
    FIATWallet getFIATWallet(User user);
    void updateBalance(User user, BigDecimal amount);
}
