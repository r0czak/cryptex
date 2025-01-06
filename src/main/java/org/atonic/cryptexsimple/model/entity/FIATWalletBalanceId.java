package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class FIATWalletBalanceId implements Serializable {
    private Long fiatWalletId;
    private Long fiatCurrencyId;
}
