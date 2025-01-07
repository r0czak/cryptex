package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "crypto_wallet")
public class CryptoWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String walletName;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "cryptoWallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CryptoWalletBalance> balances = new ArrayList<>();
}
