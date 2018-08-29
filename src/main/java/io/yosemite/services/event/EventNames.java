package io.yosemite.services.event;

public enum EventNames {
    TX_IRREVERSIBILITY("tx_irreversibility"),
    ERROR("error")
    ;

    private final String name;

    EventNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
