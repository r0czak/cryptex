package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="crypto_wallet_balance")
public class CryptoWalletBalance {
    @EmbeddedId
    private CryptoWalletBalanceId id = new CryptoWalletBalanceId();

    @ManyToOne
    @MapsId("cryptoWalletId")
    @JoinColumn(name = "crypto_wallet_id")
    private CryptoWallet cryptoWallet;

    @OneToOne
    @MapsId("cryptocurrencyId")
    @JoinColumn(name = "cryptocurrency_id")
    private Cryptocurrency cryptocurrency;

    private BigDecimal balance = BigDecimal.ZERO;
}
