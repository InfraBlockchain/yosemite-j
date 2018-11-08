package io.yosemite.services.yxcontracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeSymbol;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.util.StringUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_NFT_CONTRACT;

/**
 * Provides the APIs for the non-fungible token service.
 * There are several important decision points before creating your token.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.nft/README.md">yx.nft README</a>.
 */
public class YosemiteNonFungibleTokenJ extends AbstractToken {
    public YosemiteNonFungibleTokenJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    /**
     * Creates the token managed by the issuer.
     * Transaction fee is charged to the issuer.
     *
     * There are several important <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.token/README.md">decision points</a> before creating your token.
     * You must consider carefully because it cannot be changed once created.
     * @param symbol the symbol name; <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Convention of Symbols</a>
     * @param issuer the account name of the issuer
     * @param canSetOptions the EnumSet of {@link CanSetOptionsType} enum values
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     * @see TypeSymbol
     * @see io.yosemite.data.types.TypeYxSymbol
     */
    public CompletableFuture<PushedTransaction> createToken(
            String symbol, String issuer, EnumSet<CanSetOptionsType> canSetOptions,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        JsonArray arrayObj = new JsonArray();
        JsonObject symbolObj = getYSymbolJsonObject(0, symbol, issuer);
        arrayObj.add(symbolObj);
        arrayObj.add(AbstractToken.CanSetOptionsType.getAsBitFlags(canSetOptions));

        return pushAction(YOSEMITE_NFT_CONTRACT, "create", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Issues multiple non-fungible tokens to the <code>to</code> account by the token issuer.
     * Transaction fee is charged to the issuer.
     * @param to the account who is transferred the amount of the token
     * @param symbol the symbol name; <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-symbols">Naming Convention of Symbols</a>
     * @param issuer the account name of the issuer
     * @param ids the list of identifiers assigned by the issuer for each NFT; they are used as primary key of `nftokens` table
     * @param uris the list of item information URIs for each NFT; the number of uris must match with the number of ids
     * @param name the name of NFT; less than or equal to 32 bytes
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> issueToken(
            String to, String symbol, String issuer, Collection<Long> ids, Collection<String> uris, String name, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(symbol)) throw new IllegalArgumentException("wrong symbol");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (ids.isEmpty()) throw new IllegalArgumentException("wrong ids");
        if (uris.isEmpty()) throw new IllegalArgumentException("wrong uris");
        if (ids.size() != uris.size()) throw new IllegalArgumentException("the number of ids and uris is not matched");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("wrong name");
        if (name.length() > 32) throw new IllegalArgumentException("too long name");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject symbolObj = getYSymbolJsonObject(0, symbol, issuer);
        arrayObj.add(symbolObj);

        arrayObj.add(convertCollectionToJsonArray(ids));
        arrayObj.add(convertCollectionToJsonArray(uris));
        arrayObj.add(name);
        arrayObj.add(memo);

        return pushAction(YOSEMITE_NFT_CONTRACT, "issue", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    private JsonArray convertCollectionToJsonArray(Collection ids) {
        JsonArray idsArray = new JsonArray();
        for (Object id : ids) {
            if (id instanceof Long) {
                idsArray.add((Long)id);
            } else if (id instanceof String) {
                idsArray.add((String)id);
            }
        }
        return idsArray;
    }

    /**
     * Redeem(burn) multiple non-fungible tokens by the token issuer.
     * Transaction fee is charged to the issuer.
     * @param issuer the account name of the issuer
     * @param ids the list of identifiers assigned by the issuer for each NFT
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> redeemToken(
            String issuer, Collection<Long> ids, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (ids.isEmpty()) throw new IllegalArgumentException("wrong ids");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(issuer);
        arrayObj.add(convertCollectionToJsonArray(ids));
        arrayObj.add(memo);

        return pushAction(YOSEMITE_NFT_CONTRACT, "redeem", gson.toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{issuer + "@active"} : permissions, publicKeys);
    }

    /**
     * Transfer the arbitrary NFT from the <code>from</code> account to the <code>to</code> account.
     * It is not recommended to use if <code>from</code> account owns multiple NFTs with the same symbol.
     * Transaction fee is charged to the <code>from</code> account.
     * @param from the account name of from
     * @param to the account name of to
     * @param issuer the account name of the issuer
     * @param ids the list of identifiers assigned by the issuer for each NFT
     * @param memo data which the caller wants to save to
     * @param permissions the permission of the the <code>from</code> account
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> transferByTokenId(
            String from, String to, String issuer, Collection<Long> ids, String memo,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (memo != null && memo.length() > 256) throw new IllegalArgumentException("too long memo");

        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("issuer", issuer);
        object.add("ids", convertCollectionToJsonArray(ids));
        object.addProperty("memo", memo);

        return pushAction(YOSEMITE_NFT_CONTRACT, "transferid", gson.toJson(object),
                isEmptyArray(permissions) ? new String[]{from + "@active"} : permissions, publicKeys);
    }

    /**
     * Transfer the arbitrary NFT from the <code>from</code> account to the <code>to</code> account.
     * It is not recommended to use if <code>from</code> account owns multiple NFTs with the same symbol.
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
        return transferToken(YOSEMITE_NFT_CONTRACT, from, to, amount, issuer, memo, permissions, publicKeys);
    }

    /**
     * Set the KYC rule for token send or receipt.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#SET_KYC_RULE} was set while creating the token.
     * @param symbol the symbol name
     * @param issuer the account name of the issuer
     * @param tokenRuleType the rule type which indicates token send or receipt
     * @param kycVectors the EnumSet of {@link KYCStatusType} enum values
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @see TokenRuleType
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenKYCRule(
            String symbol, String issuer, TokenRuleType tokenRuleType, EnumSet<KYCStatusType> kycVectors,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        return setTokenKYCRule(YOSEMITE_NFT_CONTRACT, symbol, 0, issuer, tokenRuleType, kycVectors, permissions, publicKeys);
    }

    /**
     * Set the various options of the token defined by {@link TokenOptionsType}.
     * Transaction fee is charged to the issuer.
     * This action is possible only if the matching types of {@link CanSetOptionsType} were set while creating the token.
     * For example, if {@link CanSetOptionsType#FREEZE_TOKEN_TRANSFER } was set, {@link TokenOptionsType#FREEZE_TOKEN_TRANSFER} is possible to use.
     * @param symbol the symbol name
     * @param issuer the account name of the issuer
     * @param options the EnumSet of {@link TokenOptionsType} enum values
     * @param reset the flag which indicates whether the previous options are cleared or not
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setTokenOptions(
            String symbol, String issuer, EnumSet<TokenOptionsType> options, boolean reset,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        return setTokenOptions(YOSEMITE_NFT_CONTRACT, symbol, 0, issuer, options, reset, permissions, publicKeys);
    }

    /**
     * Freeze or unfreeze the accounts.
     * Transaction fee is charged to the issuer.
     * This action is possible only if {@link CanSetOptionsType#FREEZE_ACCOUNT} was set while creating the token.
     * @param symbol the symbol name
     * @param issuer the account name of the issuer
     * @param accounts the list of account names to be frozen or unfrozen
     * @param freeze the flag which indicates whether to freeze or unfreeze
     * @param permissions the permission of the issuer
     * @param publicKeys the required public keys to sign the transaction
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> freezeAccounts(
            String symbol, String issuer, final List<String> accounts, boolean freeze,
            @Nullable String[] permissions, @Nullable final String[] publicKeys) {
        return freezeAccounts(YOSEMITE_NFT_CONTRACT, symbol, 0, issuer, accounts, freeze, permissions, publicKeys);
    }

    public CompletableFuture<TableRow> getTokenStats(String symbol, String issuer) {
        return getTokenStats(YOSEMITE_NFT_CONTRACT, symbol, 0, issuer);
    }

    public CompletableFuture<TableRow> getTokenAccountBalance(String symbol, String issuer, String account) {
        return getTokenAccountBalance(YOSEMITE_NFT_CONTRACT, symbol, 0, issuer, account);
    }

    public CompletableFuture<TableRow> getTokenById(String issuer, long id) {
        if (id < 0) throw new IllegalArgumentException("wrong id");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");

        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("1"); // indicates primary key
        // defined by contracts/yx.nft/yx.nft.hpp of YosemiteChain
        options.setKeyType("i64");
        options.setLowerBound(String.valueOf(id));
        options.setLimit(1);

        return getTableRows(YOSEMITE_NFT_CONTRACT, issuer, "nftokens", options);
    }
}
