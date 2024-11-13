package org.atonic.cryptexsimple.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CryptoWalletBalanceId implements Serializable {
    private Long cryptoWalletId;
    private Long cryptocurrencyId;
}
