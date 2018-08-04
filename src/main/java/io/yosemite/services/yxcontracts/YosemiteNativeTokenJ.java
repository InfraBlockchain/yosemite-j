package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.data.remote.model.types.TypeAsset;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;

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

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nissue", new Gson().toJson(arrayObj), permissions);
    }

    public CompletableFuture<PushedTransaction> redeemNativeToken(
            final String amount, final String issuer, final String memo, final String[] permissions) {

        JsonObject object = new JsonObject();
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        object.add("amount", tokenObj);
        object.addProperty("memo", memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "nredeem", new Gson().toJson(object), permissions);
    }

    public CompletableFuture<PushedTransaction> transferNativeToken(
            final String from, final String to, final String amount, final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "transfer", new Gson().toJson(object), permissions);
    }

    public CompletableFuture<PushedTransaction> transferNativeToken(
            final String from, final String to, final String amount, final String payer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "wptransfer", new Gson().toJson(object), permissions);
    }

    private JsonObject getJsonObjectForTransfer(String from, String to, String amount, String memo) {
        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("amount", new TypeAsset(amount).toString());
        object.addProperty("memo", memo);
        return object;
    }

    public CompletableFuture<PushedTransaction> ntransferNativeToken(
            final String from, final String to, final String amount, final String issuer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForNTransfer(from, to, amount, issuer, memo);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "ntransfer", new Gson().toJson(object), permissions);
    }

    public CompletableFuture<PushedTransaction> ntransferNativeToken(
            final String from, final String to, final String token, final String issuer, final String payer,
            final String memo, final String[] permissions) {
        JsonObject object = getJsonObjectForNTransfer(from, to, token, issuer, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_NATIVE_TOKEN_CONTRACT, "wpntransfer", new Gson().toJson(object), permissions);
    }

    private JsonObject getJsonObjectForNTransfer(String from, String to, String amount, String issuer, String memo) {
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
}
