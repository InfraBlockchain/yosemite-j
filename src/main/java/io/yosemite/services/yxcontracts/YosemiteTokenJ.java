package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.data.remote.model.chain.TableRow;
import io.yosemite.data.remote.model.history.action.GetTableOptions;
import io.yosemite.data.remote.model.types.TypeAsset;
import io.yosemite.data.remote.model.types.TypeName;
import io.yosemite.data.remote.model.types.TypeSymbol;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_TOKEN_CONTRACT;

/**
 * @author Eugene Chung
 */
public class YosemiteTokenJ extends YosemiteJ {
    public YosemiteTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    public CompletableFuture<PushedTransaction> createToken(
            String symbol, int precision, String issuer, String[] permissions) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (precision < 4 || precision > 18) throw new IllegalArgumentException("wrong precision");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("symbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        arrayObj.add(symbolObj);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "create", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> issueToken(
            String to, String amount, String issuer, String memo, String[] permissions) {
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

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "issue", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> redeemToken(
            String amount, String issuer, String memo, String[] permissions) {
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        JsonObject tokenObj = new JsonObject();
        tokenObj.addProperty("amount", new TypeAsset(amount).toString());
        tokenObj.addProperty("issuer", issuer);
        arrayObj.add(tokenObj);
        arrayObj.add(memo == null? "" : memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "redeem", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> transferToken(
            String from, String to, String amount, String issuer, String memo, String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, issuer, memo);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "transfer", new Gson().toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> transferTokenWithPayer(
            String from, String to, String amount, String issuer, String payer,
            String memo, String[] permissions) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, issuer, memo);
        object.addProperty("payer", payer);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "wptransfer", new Gson().toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active", payer + "@active"} : permissions);
    }

    private JsonObject getJsonObjectForTransfer(String from, String to, String amount, String issuer, String memo) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");

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

    public CompletableFuture<TableRow> getTokenStats(String symbol, int precision, String issuer) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (precision < 4 || precision > 18) throw new IllegalArgumentException("wrong precision");

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
        if (precision < 4 || precision > 18) throw new IllegalArgumentException("wrong precision");

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

}
