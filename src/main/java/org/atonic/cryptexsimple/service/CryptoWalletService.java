package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.CryptoWallet;
import org.atonic.cryptexsimple.model.entity.User;
import org.atonic.cryptexsimple.model.entity.CryptoWalletBalance;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.math.BigDecimal;

public interface CryptoWalletService {
    CryptoWallet getCryptoWallet(User user);
    CryptoWalletBalance getBalance(User user, CryptoSymbol symbol);
    void updateBalance(User user, CryptoSymbol symbol, BigDecimal amount);
}
