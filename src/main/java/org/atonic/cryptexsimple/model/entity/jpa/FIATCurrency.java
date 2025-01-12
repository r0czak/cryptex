package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.atonic.cryptexsimple.model.enums.FIATSymbol;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "fiat_currency")
public class FIATCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FIATSymbol symbol;
}
