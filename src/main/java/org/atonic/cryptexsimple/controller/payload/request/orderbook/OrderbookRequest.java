package org.atonic.cryptexsimple.controller.payload.request.orderbook;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.io.Serializable;

@Data
public class OrderbookRequest implements Serializable {
    CryptoSymbol symbol;
}
