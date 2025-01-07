package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "crypto_wallet_balance")
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

    @Column(precision = 19, scale = 8)
    private BigDecimal balance = BigDecimal.ZERO;
}
