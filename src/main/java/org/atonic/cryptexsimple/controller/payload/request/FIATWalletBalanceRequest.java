package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FIATWalletBalanceRequest {
    BigDecimal amount;
}
