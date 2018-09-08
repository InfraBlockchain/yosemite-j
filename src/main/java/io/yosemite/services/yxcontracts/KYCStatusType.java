package io.yosemite.services.yxcontracts;

import java.util.EnumSet;

/**
 * Represents the big flags of each KYC authentication type.
 * You can use the enum values with {@link java.util.EnumSet}.
 */
public enum KYCStatusType {
    /** No authentication */
    KYC_STATUS_NO_AUTH(), // == 0
    /** E-mail authentication */
    KYC_STATUS_EMAIL_AUTH((short)0), // == 1, hereby 0 means the number of bit-shifting
    /** Phone authentication; usually SMS message */
    KYC_STATUS_PHONE_AUTH((short)1),
    /** Real name authentication provided by authentication service entities */
    KYC_STATUS_REAL_NAME_AUTH((short)2),
    /** Bank account authentication */
    KYC_STATUS_BANK_ACCOUNT_AUTH((short)3),
    ;

    private final short value;

    // Must be only used by KYC_STATUS_NO_AUTH
    KYCStatusType() {
        this.value = 0;
    }

    KYCStatusType(short shift) {
        this.value = (short) (1 << shift);
    }

    public short getValue() {
        return value;
    }

    public static short getAsBitFlags(EnumSet<KYCStatusType> flags) {
        short value = 0;
        for (KYCStatusType flag : flags) {
            value |= flag.getValue();
        }
        return value;
    }
}
