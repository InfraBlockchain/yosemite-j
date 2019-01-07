package io.yosemite.services.yxcontracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.StandardTokenConsts;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class StandardToken extends YosemiteJ implements StandardTokenConsts {
    public StandardToken(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    public String getFullTokenSymbol(int precision, String symbol) {
        return precision + "," + symbol;
    }

    /**
     * Sets token meta-information.
     * @param symbol token symbol e.g. DUSD
     * @param precision token precision e.g. 4
     * @param issuer the Yosemite account name of token issuer
     * @param url URL to represents the token issuer's identity
     * @param description token description
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenMeta(
            String symbol, int precision, String issuer, String url, String description, @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(url)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(description)) throw new IllegalArgumentException("wrong symbol");
        if (url.length() > 256) throw new IllegalArgumentException("too long url");
        if (description.length() > 256) throw new IllegalArgumentException("too long description");

        JsonArray arrayObj = new JsonArray();
        String symbolAsString = getFullTokenSymbol(precision, symbol);
        arrayObj.add(symbolAsString);
        arrayObj.add(url);
        arrayObj.add(description);

        return pushAction(issuer, ACTION_SET_TOKEN_META, gson.toJson(arrayObj), buildCommonParametersWithDefaults(params, issuer));
    }

    /**
     * Issues the amount of the token to the <code>to</code> account by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param to the account who is transferred the amount of the token
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the token issuer
     * @param tag data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueToken(
            String to, String amount, String issuer, String tag, @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (tag != null && tag.length() > 256) throw new IllegalArgumentException("too long tag");

        JsonObject object = new JsonObject();
        object.addProperty("t", issuer);
        object.addProperty("to", to);
        object.addProperty("qty", amount);
        object.addProperty("tag", tag);

        return pushAction(issuer, ACTION_ISSUE, gson.toJson(object), buildCommonParametersWithDefaults(params, issuer));
    }

    /**
     * Redeem(burn) the amount of the token by the token depository(<code>issuer</code>).
     * Transaction fee is charged to the issuer.
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the token issuer
     * @param tag data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemToken(
            String amount, String issuer, String tag, @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (tag != null && tag.length() > 256) throw new IllegalArgumentException("too long tag");

        JsonObject object = new JsonObject();
        object.addProperty("qty", amount);
        object.addProperty("tag", tag);

        return pushAction(issuer, ACTION_REDEEM, gson.toJson(object), buildCommonParametersWithDefaults(params, issuer));
    }

    public String getTransferTokenJsonString(String from, String to, String amount, String issuer, String tag) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (tag != null && tag.length() > 256) throw new IllegalArgumentException("too long tag");

        JsonObject object = new JsonObject();
        object.addProperty("t", issuer);
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("qty", amount);
        object.addProperty("tag", tag);

        return gson.toJson(object);
    }

    /**
     * Transfer the amount of the token from the <code>from</code> account to the <code>to</code> account.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param amount the amount of the token; <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md#format-of-token-amount">Format of Token Amount</a>
     * @param issuer the account name of the token issuer
     * @param tag data which the caller wants to save to
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferToken(
            String from, String to, String amount, String issuer, String tag, @Nullable TransactionParameters params) {
        return pushAction(issuer, ACTION_TRANSFER, getTransferTokenJsonString(from, to, amount, issuer, tag),
            buildCommonParametersWithDefaults(params, from));
    }
}
