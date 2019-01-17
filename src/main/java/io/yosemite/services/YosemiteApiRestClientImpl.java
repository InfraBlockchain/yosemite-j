package io.yosemite.services;

import io.yosemite.data.remote.api.*;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.util.StringUtils;

import java.util.*;

public class YosemiteApiRestClientImpl implements YosemiteApiRestClient {

    private final ApiServiceExecutor<YosemiteChainApiService> yxChainApiService;
    private final ApiServiceExecutor<YosemiteHistoryApiService> yxHistoryApiService;
    private final ApiServiceExecutor<YosemiteWalletApiService> yxWalletApiService;

    private int txExpirationInMillis;
    private String transactionVoteTarget;
    private String transactionFeePayer;

    YosemiteApiRestClientImpl(String chainBaseUrl, String walletBaseUrl, String historyBaseUrl, int txExpirationInMillis,
                              String transactionVoteTarget) {
        yxChainApiService = ApiServiceExecutor.create(YosemiteChainApiService.class, chainBaseUrl);
        yxWalletApiService = ApiServiceExecutor.create(YosemiteWalletApiService.class, walletBaseUrl);
        if (historyBaseUrl != null) {
            yxHistoryApiService = ApiServiceExecutor.create(YosemiteHistoryApiService.class, historyBaseUrl);
        } else {
            yxHistoryApiService = null;
        }
        this.txExpirationInMillis = txExpirationInMillis;
        this.transactionVoteTarget = transactionVoteTarget;
    }

    @Override
    public Request<Info> getInfo() {
        return new Request<>(yxChainApiService.getService().getInfo(), yxChainApiService);
    }

    @Override
    public Request<Block> getBlock(String blockNumberOrId) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);
        requestParameters.put("block_num_or_id", blockNumberOrId);
        return new Request<>(yxChainApiService.getService().getBlock(requestParameters), yxChainApiService);
    }

    @Override
    public Request<Account> getAccount(String accountName) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);
        requestParameters.put("account_name", accountName);
        return new Request<>(yxChainApiService.getService().getAccount(requestParameters), yxChainApiService);
    }

    @Override
    public Request<TableRow> getTableRows(String contract, String scope, String table, GetTableOptions options) {
        Map<String, String> requestParameters = new LinkedHashMap<>();

        requestParameters.put("code", contract);
        requestParameters.put("scope", scope);
        requestParameters.put("table", table);
        requestParameters.put("json", "true");

        if (options != null) {
            if (options.getLimit() > 0) {
                requestParameters.put("limit", String.valueOf(options.getLimit()));
            }
            if (!StringUtils.isEmpty(options.getLowerBound())) {
                requestParameters.put("lower_bound", options.getLowerBound());
            }
            if (!StringUtils.isEmpty(options.getUpperBound())) {
                requestParameters.put("upper_bound", options.getUpperBound());
            }
            if (!StringUtils.isEmpty(options.getIndexPosition())) {
                requestParameters.put("index_position", options.getIndexPosition());
            }
            if (!StringUtils.isEmpty(options.getKeyType())) {
                requestParameters.put("key_type", options.getKeyType());
            }
        }
        return new Request<>(yxChainApiService.getService().getTableRows(requestParameters), yxChainApiService);
    }

    @Override
    public Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req) {
        return new Request<>(yxChainApiService.getService().abiJsonToBin(req), yxChainApiService);
    }

    @Override
    public Request<AbiBinToJsonResponse> abiBinToJson(AbiBinToJsonRequest req) {
        return new Request<>(yxChainApiService.getService().abiBinToJson(req), yxChainApiService);
    }

    @Override
    public Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest req) {
        return new Request<>(yxChainApiService.getService().getRequiredKeys(req), yxChainApiService);
    }

    @Override
    public Request<TokenInfo> getTokenInfo(String token) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("token", token);
        return new Request<>(yxChainApiService.getService().getTokenInfo(requestParameters), yxChainApiService);
    }

    @Override
    public Request<TypeAsset> getTokenBalance(String token, String account) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("token", token);
        requestParameters.put("account", account);
        return new Request<>(yxChainApiService.getService().getTokenBalance(requestParameters), yxChainApiService);
    }

    @Override
    public Request<PushedTransaction> pushTransaction(PackedTransaction packedTransaction) {
        return new Request<>(yxChainApiService.getService().pushTransaction(packedTransaction), yxChainApiService);
    }

    @Override
    public Request<List<String>> getPublicKeys() {
        return new Request<>(yxWalletApiService.getService().getPublicKeys(), yxWalletApiService);
    }

    @Override
    public Request<String> createKey() {
        return createKey("default");
    }

    @Override
    public Request<String> createKey(String walletName) {
        return createKey("default", "k1");
    }

    @Override
    public Request<String> createKey(String walletName, String keyType) {
        if (StringUtils.isEmpty(walletName)) throw new IllegalArgumentException();
        if (StringUtils.isEmpty(keyType)) throw new IllegalArgumentException();

        Collection collectionReq = new ArrayList();

        collectionReq.add(walletName);
        collectionReq.add(keyType);

        return new Request<>(yxWalletApiService.getService().createKey(collectionReq), yxWalletApiService);
    }

    @Override
    public Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> publicKeys, String chainId) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(publicKeys);
        collectionReq.add(chainId);

        return new Request<>(yxWalletApiService.getService().signTransaction(collectionReq), yxWalletApiService);
    }

    @Override
    public Request<String> signDigest(String hexData, String pubKey) {

        Collection collectionReq = new ArrayList();

        collectionReq.add(hexData);
        collectionReq.add(pubKey);

        return new Request<>(yxWalletApiService.getService().signDigest(collectionReq), yxWalletApiService);
    }

    @Override
    public Request<io.yosemite.data.remote.history.transaction.Transaction> getTransaction(String txId) {
        if (yxHistoryApiService == null) throw new IllegalStateException("Chain explorer URL is not set");
        return new Request<>(yxHistoryApiService.getService().getTransaction(txId), yxHistoryApiService, false);
    }

    @Override
    public Request<Actions> getActions(String accountName, long startPosition, int offset) {
        if (yxHistoryApiService == null) throw new IllegalStateException("Chain explorer URL is not set");
        return new Request<>(yxHistoryApiService.getService().getActions(accountName, startPosition, offset), yxHistoryApiService, false);
    }

    @Override
    public int getTxExpirationInMillis() {
        return txExpirationInMillis;
    }

    @Override
    public void setTxExpirationInMillis(int txExpirationInMillis) {
        this.txExpirationInMillis = txExpirationInMillis;
    }

    @Override
    public String getTransactionVoteTarget() {
        return transactionVoteTarget;
    }

    @Override
    public void setTransactionVoteTarget(String transactionVoteTarget) {
        this.transactionVoteTarget = transactionVoteTarget;
    }

    public String getTransactionFeePayer() {
        return transactionFeePayer;
    }

    public void setTransactionFeePayer(String transactionFeePayer) {
        this.transactionFeePayer = transactionFeePayer;
    }
}
