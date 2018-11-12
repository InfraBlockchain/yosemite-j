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
public class YosemiteTokenJ extends AbstractToken {
    public YosemiteTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    private void checkPrecision(int precision) {
        if (precision < 2 || precision > 18) throw new IllegalArgumentException("wrong precision");
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

        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getYSymbolJsonObject(precision, symbol, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(CanSetOptionsType.getAsBitFlags(canSetOptions));

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "create", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
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
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemToken(
            String amount, String issuer, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
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
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferToken(
            String from, String to, String amount, String issuer, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        return transferToken(YOSEMITE_TOKEN_CONTRACT, from, to, amount, issuer, memo, permissions, publicKeys);
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
        checkPrecision(precision);
        return setTokenKYCRule(YOSEMITE_TOKEN_CONTRACT, symbol, precision, issuer, tokenRuleType, kycVectors, permissions, publicKeys);
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
        checkPrecision(precision);
        return setTokenOptions(YOSEMITE_TOKEN_CONTRACT, symbol, precision, issuer, options, reset, permissions, publicKeys);
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
        checkPrecision(precision);
        return freezeAccounts(YOSEMITE_TOKEN_CONTRACT, symbol, precision, issuer, accounts, freeze, permissions, publicKeys);
    }

    /**
     * Sets the limit of user issue amount by the token issuer.
     * @param to the account who is set the user issue limit
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setUserIssueLimit(
            String to, String amount, String issuer, @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "setuilimit", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Issues the amount of the token by the <code>user</code> who is granted the user issue authority.
     * Transaction fee is charged to the user.
     * @param user the account who is granted the user issue authority
     * @param to the account who is transferred the token
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the user or issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueTokenByUser(
            String user, String to, String amount, String issuer, @Nullable String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(user)) throw new IllegalArgumentException("wrong user");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(user);
        arrayObj.add(to);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "issuebyuser", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{user + "@active"} : permissions, publicKeys);
    }

    /**
     * Entrust the user issue authority to another account.
     * Transaction fee is charged to the user.
     * Even if the entrustment is settled, the user still can do `user issue` by himself or herself.
     * If <code>to</code> account is set to <code>user</code>, it means cancellation of entrustment.
     * @param user the account who is granted the user issue authority
     * @param to the account who is entrusted the user issue; Currently it must be the token issuer or user.
     * @param symbol the symbol name; <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Convention of Symbols</a>
     * @param precision the number of bits used to hold the fractional part in the concept of floating-point numbers; from 4 to 18
     * @param issuer the account name of the issuer
     * @param permissions the permission of the user
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> entrustUserIssueTo(
            String user, String to, String symbol, int precision, String issuer,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(user)) throw new IllegalArgumentException("wrong user");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(user);
        arrayObj.add(to);
        JsonObject symbolObj = getYSymbolJsonObject(precision, symbol, issuer);
        arrayObj.add(symbolObj);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "entrustui", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{user + "@active"} : permissions, publicKeys);
    }

    /**
     * Decreases or increases the total user-issued amount of token of the user.
     * Transaction fee is charged to the issuer.
     * @param user the account who is granted the issue authority
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param decrease boolean(T/F) field to indicate decrement or increment of token amount
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> changeIssuedTokenAmount(
            String user, String amount, String issuer, boolean decrease, @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(user)) throw new IllegalArgumentException("wrong user");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(user);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(decrease ? 1 : 0);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "changeissued", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    public CompletableFuture<TableRow> getTokenStats(String symbol, int precision, String issuer) {
        return getTokenStats(YOSEMITE_TOKEN_CONTRACT, symbol, precision, issuer);
    }

    public CompletableFuture<TableRow> getTokenAccountBalance(String symbol, int precision, String issuer, String account) {
        return getTokenAccountBalance(YOSEMITE_TOKEN_CONTRACT, symbol, precision, issuer, account);
    }

    public CompletableFuture<TableRow> getTokenDelegatedIssue(String symbol, int precision, String issuer, String account) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");
        checkPrecision(precision);

        String secondaryKeySerializedHex = Utils.makeWebAssembly128BitIntegerAsHexString(
                TypeName.stringToName(issuer), TypeName.stringToName(account));

        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("2"); // indicates secondary index
                                       // defined by contracts/yx.token/yx.token.hpp of YosemiteChain
        options.setKeyType("i128");
        options.setLowerBound(secondaryKeySerializedHex);
        options.setLimit(1);

        return getTableRows(YOSEMITE_TOKEN_CONTRACT, new TypeSymbol(precision, symbol).toString(), "delissue", options);
    }
}
