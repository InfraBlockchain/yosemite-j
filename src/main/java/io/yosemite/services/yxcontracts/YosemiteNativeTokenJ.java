package io.yosemite.services.yxcontracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.Consts.YOSEMITE_NATIVE_TOKEN_CONTRACT;

/**
 * Provides the APIs for the native token service.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md">yx.ntoken README</a>.
 */
public class YosemiteNativeTokenJ extends YosemiteJ {
    public YosemiteNativeTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    /**
     * Issues the amount of the native token to the <code>to</code> account by the system depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param to the account who is transferred the amount of the native token
     * @param amount the amount of the native token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueNativeToken(
            final String to, final String amount, final String issuer, final String memo,
            @Nullable TransactionParameters params) {
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
        arrayObj.add(memo == null? "" : memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nissue", gson.toJson(arrayObj),
                buildCommonParametersWithDefaults(params, issuer));
    }

    /**
     * Redeem(burn) the amount of the native token by the system depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param amount the amount of the native token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the issuer
     * @param memo data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemNativeToken(
            final String amount, final String issuer, final String memo,
            @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonObject object = new JsonObject();
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        object.add("token", tokenObj);
        object.addProperty("memo", memo == null? "" : memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nredeem", gson.toJson(object),
                buildCommonParametersWithDefaults(params, issuer));
    }

    /**
     * Transfer the amount of the native token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the native token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param memo data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferNativeToken(
            final String from, final String to, final String amount, final String memo,
            @Nullable TransactionParameters params) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "transfer", gson.toJson(object),
                buildCommonParametersWithDefaults(params, from));
    }

    private JsonObject getJsonObjectForTransfer(String from, String to, String amount, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("amount", new TypeAsset(amount).toString());
        object.addProperty("memo", memo);
        return object;
    }

    /**
     * Transfer the amount of the native token with the designated issuer from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the native token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the native token issuer
     * @param memo data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> ntransferNativeToken(
            final String from, final String to, final String amount, final String issuer,
            final String memo, @Nullable TransactionParameters params) {
        JsonObject object = getJsonObjectForNTransfer(from, to, amount, issuer, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "ntransfer", gson.toJson(object),
                buildCommonParametersWithDefaults(params, from));
    }

    private JsonObject getJsonObjectForNTransfer(String from, String to, String amount, String issuer, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonObject object = new JsonObject();

        object.addProperty("from", from);
        object.addProperty("to", to);

        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        object.add("token", tokenObj);

        object.addProperty("memo", memo == null? "" : memo);
        return object;
    }

    public CompletableFuture<TableRow> getNativeTokenStats(final String issuer) {
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        GetTableOptions options = new GetTableOptions();
        options.setLimit(1);

        return getTableRows(YOSEMITE_NATIVE_TOKEN_CONTRACT, issuer, "ntstats", options);
    }

    public CompletableFuture<TableRow> getNativeTokenAccountBalance(final String account) {
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");

        GetTableOptions options = new GetTableOptions();
        options.setLimit(1);

        return getTableRows(YOSEMITE_NATIVE_TOKEN_CONTRACT, account, "ntaccounts", options);
    }

    public CompletableFuture<TableRow> getNativeTokenAccountTotalBalance(final String account) {
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");

        GetTableOptions options = new GetTableOptions();
        options.setLimit(1);

        return getTableRows(YOSEMITE_NATIVE_TOKEN_CONTRACT, account, "ntaccountstt", options);
    }

}
