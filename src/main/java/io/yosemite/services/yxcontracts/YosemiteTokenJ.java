package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
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

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_TOKEN_CONTRACT;

/**
 * @author Eugene Chung
 */
public class YosemiteTokenJ extends YosemiteJ {
    public YosemiteTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    private void checkPrecision(int precision) {
        if (precision < 4 || precision > 18) throw new IllegalArgumentException("wrong precision");
    }

    public CompletableFuture<PushedTransaction> createToken(
            String symbol, int precision, String issuer, EnumSet<CanSetOptionsType> canSetOptions, String[] permissions) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("symbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(CanSetOptionsType.getAsBitFlags(canSetOptions));

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

    public CompletableFuture<PushedTransaction> setTokenKYCRule(
            String symbol, int precision, String issuer, KYCRuleType kycRuleType, EnumSet<KYCStatusType> kycVectors, String[] permissions) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("symbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(kycRuleType.getValue());
        arrayObj.add(KYCStatusType.getAsBitFlags(kycVectors));

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "setkycrule", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> setTokenOptions(
            String symbol, int precision, String issuer, EnumSet<TokenOptionsType> options, boolean overwrite, String[] permissions) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("symbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(TokenOptionsType.getAsBitFlags(options));
        arrayObj.add(overwrite ? 1 : 0);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "setoptions", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> freezeAccounts(
            String symbol, int precision, String issuer, final List<String> accounts, boolean freeze, final String[] permissions) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        checkPrecision(precision);
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (accounts == null || accounts.isEmpty()) throw new IllegalArgumentException("empty accounts");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("symbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        arrayObj.add(symbolObj);

        JsonArray accountsObj = new JsonArray();
        for (String account : accounts) {
            accountsObj.add(account);
        }
        arrayObj.add(accountsObj);
        arrayObj.add(freeze ? 1 : 0);

        return pushAction(YOSEMITE_TOKEN_CONTRACT, "freezeacc", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions);
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
     * Set<CanSetOptionsType> statusFlags = EnumSet.of(
     *             CanSetOptionsType.FREEZE_TOKEN_TRANSFER,
     *             CanSetOptionsType.FREEZE_ACCOUNT);
     */
    public enum CanSetOptionsType {
        NONE(), // == 0
        FREEZE_TOKEN_TRANSFER((short)0),
        FREEZE_ACCOUNT((short)1),
        SET_KYC_RULE((short)2),
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
     * KYC Rule Type for Non-native Token
     */
    public enum KYCRuleType {
        KYC_RULE_TRANSFER_SEND((short)0),
        KYC_RULE_TRANSFER_RECEIVE((short)1)
        ;

        private final short value;

        KYCRuleType(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    public enum TokenOptionsType {
        NONE((short)0),
        FREEZE_TOKEN_TRANSFER((short)1)
        ;

        private final short value;

        TokenOptionsType(short value) {
            this.value = value;
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
