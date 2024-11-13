package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="trade")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime timestamp;

    @ManyToOne
    private TradeOrder buyTradeOrder;
    @ManyToOne
    private TradeOrder sellTradeOrder;
    @ManyToOne
    private Cryptocurrency cryptocurrency;
}
