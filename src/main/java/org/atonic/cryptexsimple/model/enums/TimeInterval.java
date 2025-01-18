package org.atonic.cryptexsimple.model.enums;

public enum TimeInterval {
    ONE_MINUTE("1m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    THIRTY_MINUTES("30m"),
    ONE_HOUR("1h"),
    FOUR_HOURS("4h"),
    ONE_DAY("1d");

    private final String value;

    TimeInterval(String value) {
        this.value = value;
    }
}
