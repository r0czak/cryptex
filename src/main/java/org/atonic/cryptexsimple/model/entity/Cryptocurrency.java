package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name="cryptocurrency")
public class Cryptocurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CryptoSymbol symbol;
}
