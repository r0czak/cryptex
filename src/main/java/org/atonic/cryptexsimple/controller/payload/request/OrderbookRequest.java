package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;
import org.atonic.cryptexsimple.model.enums.CryptoSymbol;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderbookRequest implements Serializable {
    CryptoSymbol symbol;
}
