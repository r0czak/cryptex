package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atonic.cryptexsimple.model.enums.OrderStatus;
import org.atonic.cryptexsimple.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "trade_order")
public class TradeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderType type;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal amount;
    private BigDecimal price;

    private LocalDateTime timestamp;

    @ManyToOne
    private User user;

    @ManyToOne
    private CryptoWallet cryptoWallet;

    @ManyToOne
    private Cryptocurrency cryptocurrency;

    @ManyToOne
    private FIATWallet fiatWallet;

    @ManyToOne
    FIATCurrency fiatCurrency;
}
