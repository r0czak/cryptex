package org.atonic.cryptexsimple.model.enums;

public enum CryptoSymbol {
    BTC("Bitcoin"),
    ETH("Ethereum"),
    LTC("Litecoin");

    public final String value;

    CryptoSymbol(String value) {
        this.value = value;
    }
}
