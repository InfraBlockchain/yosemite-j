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

public class AbstractToken extends YosemiteJ {
    protected AbstractToken(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
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
     * You can use the enum values with {@link EnumSet}.
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

    JsonObject getJsonObjectForTransfer(String from, String to, String amount, String issuer, String memo) {
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

        object.addProperty("memo", memo);
        return object;
    }

    JsonObject getTokenJsonObject(String eosSymbolStr, String issuer) {
        JsonObject symbolObj = new JsonObject();
        symbolObj.addProperty("tsymbol", eosSymbolStr);
        symbolObj.addProperty("issuer", issuer);
        return symbolObj;
    }

    CompletableFuture<PushedTransaction> transferToken(
            String contract, String from, String to, String amount, String issuer, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        JsonObject object = getJsonObjectForTransfer(from, to, amount, issuer, memo);

        return pushAction(contract, "transfer", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions, publicKeys);
    }

    CompletableFuture<PushedTransaction> setTokenKYCRule(
            String contract, String symbol, int precision, String issuer, TokenRuleType tokenRuleType, EnumSet<KYCStatusType> kycVectors,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(tokenRuleType.getValue());
        arrayObj.add(KYCStatusType.getAsBitFlags(kycVectors));

        return pushAction(contract, "setkycrule", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    CompletableFuture<PushedTransaction> setTokenOptions(
            String contract, String symbol, int precision, String issuer, EnumSet<TokenOptionsType> options, boolean reset,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;
        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getTokenJsonObject(eosSymbolStr, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(TokenOptionsType.getAsBitFlags(options));
        arrayObj.add(reset ? 1 : 0);

        return pushAction(contract, "setoptions", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    CompletableFuture<PushedTransaction> freezeAccounts(
            String contract, String symbol, int precision, String issuer, final List<String> accounts, boolean freeze,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
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

        return pushAction(contract, "freezeacc", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    CompletableFuture<TableRow> getTokenStats(String contract, String symbol, int precision, String issuer) {
        if (StringUtils.isEmpty(contract)) throw new IllegalArgumentException("wrong contract");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        String eosSymbolStr = precision + "," + symbol;

        GetTableOptions options = new GetTableOptions();
        options.setLowerBound(String.valueOf(TypeName.stringToName(issuer)));
        options.setLimit(1);

        return getTableRows(contract, eosSymbolStr, "tstats", options);
    }

    CompletableFuture<TableRow> getTokenAccountBalance(String contract, String symbol, int precision, String issuer, String account) {
        if (StringUtils.isEmpty(contract)) throw new IllegalArgumentException("wrong contract");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");

        String yxSymbolSerializedHex = Utils.makeWebAssembly128BitIntegerAsHexString(
                TypeSymbol.stringToSymbol(precision, symbol), TypeName.stringToName(issuer));

        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("2"); // indicates secondary index 'yxsymbol' of accounts
                                       // defined by contracts/yx.token/yx.token.hpp of YosemiteChain
        options.setKeyType("i128");
        options.setLowerBound(yxSymbolSerializedHex);
        options.setLimit(1);

        return getTableRows(contract, account, "taccounts", options);
    }
}
