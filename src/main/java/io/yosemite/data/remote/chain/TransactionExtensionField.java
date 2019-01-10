package io.yosemite.data.remote.chain;

public enum TransactionExtensionField {
    TRANSACTION_VOTE_ACCOUNT((short)1001),
    TRANSACTION_FEE_PAYER((short)1002)
    ;

    private final short value;

    TransactionExtensionField(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
