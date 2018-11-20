package io.yosemite;

public final class Consts {

    public static final String OWNER_PERMISSION_NAME = "owner";
    public static final String ACTIVE_PERMISSION_NAME = "active";

    public static final String YOSEMITE_SYSTEM_CONTRACT = "yosemite";
    public static final String YOSEMITE_NATIVE_TOKEN_CONTRACT = "yx.ntoken";
    public static final String YOSEMITE_TOKEN_CONTRACT = "yx.token";
    public static final String YOSEMITE_DIGITAL_CONTRACT_CONTRACT = "yx.dcontract";
    public static final String YOSEMITE_TOKEN_ESCROW_CONTRACT = "yx.escrow";
    public static final String YOSEMITE_NFT_CONTRACT = "yx.nft";

    public static final String DEFAULT_KEYOS_HTTP_URL = "http://127.0.0.1:8900";
    public static final String DEFAULT_WALLET_NAME = "default";
    public static final boolean DEFAULT_SAVE_PASSWORD = true;

    public static final int TX_EXPIRATION_IN_MILLIS = 1000 * 60 * 2; // 2 minutes (from cleos)

    public static final String DEFAULT_SYMBOL_STRING = "DKRW";
    public static final int DEFAULT_SYMBOL_PRECISION = 2;
}
