package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="crypto_wallet")
public class CryptoWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    private User user;

    @OneToMany(mappedBy = "cryptoWallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CryptoWalletBalance> balances = new ArrayList<>();
}
