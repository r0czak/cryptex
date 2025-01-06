package org.atonic.cryptexsimple.model.entity;

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
@Table(name = "fiat_wallet_balance")
public class FIATWalletBalance {
    @EmbeddedId
    private FIATWalletBalanceId id = new FIATWalletBalanceId();

    @ManyToOne
    @MapsId("fiatWalletId")
    @JoinColumn(name = "fiat_wallet_id")
    private FIATWallet fiatWallet;

    @OneToOne
    @MapsId("fiatCurrencyId")
    @JoinColumn(name = "fiat_currency_id")
    private FIATCurrency fiatCurrency;

    @Column(precision = 13, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
}
