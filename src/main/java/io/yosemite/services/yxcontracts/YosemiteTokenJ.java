package io.yosemite.services.yxcontracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.data.types.TypeName;
import io.yosemite.data.types.TypeSymbol;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_TOKEN_CONTRACT;

/**
 * Provides the APIs for the non-native or user token service.
 * There are several important decision points before creating your token.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.token/README.md">yx.token README</a>.
 */
public class YosemiteTokenJ extends YosemiteJ {
    public YosemiteTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    private void checkPrecision(int precision) {
        if (precision < 4 || precision > 18) throw new IllegalArgumentException("wrong precision");
    }

    /**
     * Creates the token managed by the issuer.
     * Transaction fee is charged to the issuer.
     *
     * There are several important <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.token/README.md">decision points</a> before creating your token.
     * You must consider carefully because it cannot be changed once created.
     * @param symbol the symbol name; <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Convention of Symbols</a>
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers; from 4 to 18
     * @param issuer the account name of the issuer
     * @param canSetOptions the EnumSet of {@link CanSetOptionsType} enum values
     * @param permissions the permission of the issuer
     * @return CompletableFuture instance to get PushedTransaction instance
     * @see TypeSymbol
     * @see io.yosemite.data.types.TypeYxSymbol
     */
    public CompletableFuture<PushedTransaction> createToken(
            String symbol, int precision, String issuer, EnumSet<CanSetOptionsType> canSetOptions,
            @Nullable String[] permissions) {
        return createToken(symbol, precision, issuer, canSetOptions, permissions, null);
    }

    /**
     * Creates the token managed by the issuer.
     * Transaction fee is charged to the issuer.
     *
     * There are several important <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.token/README.md">decision points</a> before creating your token.
     * You must consider carefully because it cannot be changed once created.
     * @param symbol the symbol name; <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Convention of Symbols</a>
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers; from 4 to 18
     * @param issuer the account name of the issuer
     * @param canSetOptions the EnumSet of {@link CanSetOptionsType} enum values
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     * @see TypeSymbol
     * @see io.yosemite.data.types.TypeYxSymbol
     */
    public CompletableFuture<PushedTransaction> createToken(
            String symbol, int precision, String issuer, EnumSet<CanSetOptionsType> canSetOptions,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(CanSetOptionsType.getAsBitFlags(canSetOptions));

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "create", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    private JsonObject getTokenJsonObject(String eosSymbolStr, String issuer) {
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("tsymbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        return symbolObj;
    }

    /**
     * Issues the amount of the token to the <code>to</code> account by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param to the account who is transferred the amount of the token
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueToken(
            String to, String amount, String issuer, String memo, String[] permissions) {
        return issueToken(to, amount, issuer, memo, permissions, null);
    }

    /**
     * Issues the amount of the token to the <code>to</code> account by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param to the account who is transferred the amount of the token
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueToken(
            String to, String amount, String issuer, String memo, @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "issue", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Redeem(burn) the amount of the token by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemToken(
            String amount, String issuer, String memo, @Nullable String[] permissions) {
        return redeemToken(amount, issuer, memo, permissions, null);
    }

    /**
     * Redeem(burn) the amount of the token by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemToken(
            String amount, String issuer, String memo, @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonArray arrayObj = new JsonArray();
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "redeem", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Transfer the amount of the token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the the <code>from</code> account
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferToken(
            String from, String to, String amount, String issuer, String memo, @Nullable String[] permissions) {
        return transferToken(from, to, amount, issuer, memo, permissions, null);
    }

    /**
     * Transfer the amount of the token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the the <code>from</code> account
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferToken(
            String from, String to, String amount, String issuer, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, issuer, memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "transfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions, publicKeys);
    }

    /**
     * Transfer the amount of the token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>payer</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param payer the account name of the transaction fee payer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the the <code>from</code> account and the <code>payer</code> account
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferTokenWithPayer(
            String from, String to, String amount, String issuer, String payer,
            String memo, @Nullable String[] permissions) {
        return transferTokenWithPayer(from, to, amount, issuer, payer, memo, permissions, null);
    }

    /**
     * Transfer the amount of the token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>payer</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param payer the account name of the transaction fee payer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the the <code>from</code> account and the <code>payer</code> account
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferTokenWithPayer(
            String from, String to, String amount, String issuer, String payer,
            String memo, @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, issuer, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "wptransfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active", payer + "@active"} : permissions, publicKeys);
    }

    private JsonObject getJsonObjectForTransfer(String from, String to, String amount, String issuer, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);

        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        object.add("token", tokenObj);

        object.addProperty("memo", memo);
        return object;
    }

    /**
     * Set the KYC rule for token send or receipt.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#SET_KYC_RULE} was set while creating the token.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param tokenRuleType the rule type which indicates token send or receipt
     * @param kycVectors the EnumSet of {@link KYCStatusType} enum values
     * @param permissions the permission of the issuer
     * @see TokenRuleType
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenKYCRule(
            String symbol, int precision, String issuer, TokenRuleType tokenRuleType, EnumSet<KYCStatusType> kycVectors, @Nullable String[] permissions) {
        return setTokenKYCRule(symbol, precision, issuer, tokenRuleType, kycVectors, permissions, null);
    }

    /**
     * Set the KYC rule for token send or receipt.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#SET_KYC_RULE} was set while creating the token.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param tokenRuleType the rule type which indicates token send or receipt
     * @param kycVectors the EnumSet of {@link KYCStatusType} enum values
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @see TokenRuleType
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenKYCRule(
            String symbol, int precision, String issuer, TokenRuleType tokenRuleType, EnumSet<KYCStatusType> kycVectors,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(tokenRuleType.getValue());
        arrayObj.add(KYCStatusType.getAsBitFlags(kycVectors));

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "setkycrule", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Set the various options of the token defined by {@link TokenOptionsType}.
     * Transaction fee is charged to the issuer.
     * This action is possible only if the matching types of {@link CanSetOptionsType} were set while creating the token.
     * For example, if {@link CanSetOptionsType#FREEZE_TOKEN_TRANSFER } was set, {@link TokenOptionsType#FREEZE_TOKEN_TRANSFER} is possible to use.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param options the EnumSet of {@link TokenOptionsType} enum values
     * @param reset the flag which indicates whether the previous options are cleared or not
     * @param permissions the permission of the issuer
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenOptions(
            String symbol, int precision, String issuer, EnumSet<TokenOptionsType> options, boolean reset,
            @Nullable String[] permissions) {
        return setTokenOptions(symbol, precision, issuer, options, reset, permissions, null);
    }

    /**
     * Set the various options of the token defined by {@link TokenOptionsType}.
     * Transaction fee is charged to the issuer.
     * This action is possible only if the matching types of {@link CanSetOptionsType} were set while creating the token.
     * For example, if {@link CanSetOptionsType#FREEZE_TOKEN_TRANSFER } was set, {@link TokenOptionsType#FREEZE_TOKEN_TRANSFER} is possible to use.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param options the EnumSet of {@link TokenOptionsType} enum values
     * @param reset the flag which indicates whether the previous options are cleared or not
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenOptions(
            String symbol, int precision, String issuer, EnumSet<TokenOptionsType> options, boolean reset,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(TokenOptionsType.getAsBitFlags(options));
        arrayObj.add(reset ? 1 : 0);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "setoptions", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Freeze or unfreeze the accounts.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#FREEZE_ACCOUNT} was set while creating the token.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param accounts the list of account names to be frozen or unfrozen
     * @param freeze the flag which indicates whether to freeze or unfreeze
     * @param permissions the permission of the issuer
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> freezeAccounts(
            String symbol, int precision, String issuer, final List<String> accounts, boolean freeze, @Nullable String[] permissions) {
        return freezeAccounts(symbol, precision, issuer, accounts, freeze, permissions, null);
    }

    /**
     * Freeze or unfreeze the accounts.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#FREEZE_ACCOUNT} was set while creating the token.
     * @param symbol the symbol name
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers
     * @param issuer the account name of the issuer
     * @param accounts the list of account names to be frozen or unfrozen
     * @param freeze the flag which indicates whether to freeze or unfreeze
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> freezeAccounts(
            String symbol, int precision, String issuer, final List<String> accounts, boolean freeze,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (accounts == null || accounts.isEmpty()) throw new IllegalArgumentException("empty accounts");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);

        JsonArray accountsObj = new JsonArray();
        for (String account : accounts) {
            accountsObj.add(account);
        }
        arrayObj.add(accountsObj);
        arrayObj.add(freeze ? 1 : 0);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "freezeacc", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    public CompletableFuture<TableRow> getTokenStats(String symbol, int precision, String issuer) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        checkPrecision(precision);

        String eosSymbolStr = precision + "," + symbol;

        GetTableOptions options = new GetTableOptions();
        options.setLowerBound(String.valueOf(TypeName.string_to_name(issuer)));
        options.setLimit(1);

        return getTableRows(YOSEMITE_TOKEN_CONTRACT, eosSymbolStr, "tstats", options);
    }

    public CompletableFuture<TableRow> getTokenAccountBalance(String symbol, int precision, String issuer, String account) {
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        checkPrecision(precision);

        String yxSymbolSerializedHex = Utils.makeWebAssembly128BitIntegerAsHexString(
                TypeSymbol.stringToSymbol(precision, symbol), TypeName.string_to_name(issuer));

        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("2"); // indicates secondary index 'yxsymbol' of accounts
                                     // defined by contracts/yx.token/yx.token.hpp of YosemiteChain
        options.setKeyType("i128");
        options.setLowerBound(yxSymbolSerializedHex);
        options.setLimit(1);

        return getTableRows(YOSEMITE_TOKEN_CONTRACT, account, "taccounts", options);
    }

    /**
     * Represents the big flags of each can-set-options value.
     * You can use the enum values with {@link java.util.EnumSet}.
     */
    public enum CanSetOptionsType {
        NONE(), // == 0
        FREEZE_TOKEN_TRANSFER((short)0), // == 1, hereby 0 means the number of bit-shifting
        FREEZE_ACCOUNT((short)1),
        SET_KYC_RULE((short)2),
        SET_ACCOUNT_TYPE_RULE((short)3),
        ;

        private final short value;

        // Must be only used by NONE
        CanSetOptionsType() {
            this.value = 0;
        }

        CanSetOptionsType(short shift) {
            this.value = (short) (1 << shift);
        }

        public short getValue() {
            return value;
        }

        public static short getAsBitFlags(EnumSet<CanSetOptionsType> flags) {
            short value = 0;
            for (CanSetOptionsType flag : flags) {
                value |= flag.getValue();
            }
            return value;
        }
    }

    /**
     * Rules for non-native token
     */
    public enum TokenRuleType {
        KYC_RULE_TRANSFER_SEND((short)0),
        KYC_RULE_TRANSFER_RECEIVE((short)1)
        ;

        private final short value;

        TokenRuleType(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    /**
     * Represents the big flags of each token option.
     * You can use the enum values with {@link java.util.EnumSet}.
     */
    public enum TokenOptionsType {
        NONE(),
        FREEZE_TOKEN_TRANSFER((short)0) // == 1, hereby 0 means the number of bit-shifting
        ;

        private final short value;

        // Must be only used by NONE
        TokenOptionsType() {
            this.value = 0;
        }

        TokenOptionsType(short shift) {
            this.value = (short) (1 << shift);
        }

        public short getValue() {
            return value;
        }

        public static short getAsBitFlags(EnumSet<TokenOptionsType> flags) {
            short value = 0;
            for (TokenOptionsType flag : flags) {
                value |= flag.getValue();
            }
            return value;
        }
    }
}
