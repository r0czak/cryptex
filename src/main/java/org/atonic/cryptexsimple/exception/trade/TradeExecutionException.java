package org.atonic.cryptexsimple.exception.trade;

public class TradeExecutionException extends Exception {
    public TradeExecutionException(String message) {
        super(message);
    }

    public TradeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
