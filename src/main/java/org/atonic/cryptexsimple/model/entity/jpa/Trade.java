package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "trade")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 13, scale = 2)
    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime timestamp;

    @ManyToOne
    private User seller;
    @ManyToOne
    private FIATWallet sellerFIATWallet;
    @ManyToOne
    private CryptoWallet sellerCryptoWallet;


    @ManyToOne
    private User buyer;
    @ManyToOne
    private FIATWallet buyerFIATWallet;
    @ManyToOne
    private CryptoWallet buyerCryptoWallet;


    @ManyToOne
    private FIATCurrency fiatCurrency;
    @ManyToOne
    private Cryptocurrency cryptocurrency;
}
