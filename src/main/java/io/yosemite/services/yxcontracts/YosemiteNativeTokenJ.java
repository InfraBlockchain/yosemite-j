package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;

import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_NATIVE_TOKEN_CONTRACT;

/**
 * @author Eugene Chung
 */
public class YosemiteNativeTokenJ extends YosemiteJ {
    public YosemiteNativeTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    public CompletableFuture<PushedTransaction> issueNativeToken(
            final String to, final String amount, final String issuer, final String memo, final String[] permissions) {
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo == null? "" : memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nissue", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> redeemNativeToken(
            final String amount, final String issuer, final String memo, final String[] permissions) {
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonObject object = new JsonObject();
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        object.add("token", tokenObj);
        object.addProperty("memo", memo == null? "" : memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nredeem", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> transferNativeToken(
            final String from, final String to, final String amount, final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "transfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> transferNativeTokenWithPayer(
            final String from, final String to, final String amount, final String payer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "wptransfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active", payer + "@active"} : permissions);
    }

    private JsonObject getJsonObjectForTransfer(String from, String to, String amount, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");

        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("amount", new TypeAsset(amount).toString());
        object.addProperty("memo", memo);
        return object;
    }

    public CompletableFuture<PushedTransaction> ntransferNativeToken(
            final String from, final String to, final String token, final String issuer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForNTransfer(from, to, token, issuer, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "ntransfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> ntransferNativeTokenWithPayer(
            final String from, final String to, final String token, final String issuer, final String payer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForNTransfer(from, to, token, issuer, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "wpntransfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active", payer + "@active"} : permissions);
    }

    private JsonObject getJsonObjectForNTransfer(String from, String to, String amount, String issuer, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

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
