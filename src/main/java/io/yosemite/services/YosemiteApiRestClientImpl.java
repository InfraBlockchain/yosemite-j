package io.yosemite.services;

import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.remote.history.controlledaccounts.ControlledAccounts;
import io.yosemite.data.remote.history.keyaccounts.KeyAccounts;
import io.yosemite.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class YosemiteApiRestClientImpl implements YosemiteApiRestClient {

    private final ApiServiceExecutor<YosemiteChainApiService> yxChainApiService;
    private final ApiServiceExecutor<YosemiteHistoryApiService> yxHistoryApiService;
    private final ApiServiceExecutor<YosemiteWalletApiService> yxWalletApiService;

    private final int txExpirationInMillis;

    YosemiteApiRestClientImpl(String chainBaseUrl, String walletBaseUrl, String historyBaseUrl, int txExpirationInMillis) {
        yxChainApiService = ApiServiceExecutor.create(YosemiteChainApiService.class, chainBaseUrl);
        yxWalletApiService = ApiServiceExecutor.create(YosemiteWalletApiService.class, walletBaseUrl);
        yxHistoryApiService = ApiServiceExecutor.create(YosemiteHistoryApiService.class, historyBaseUrl);
        this.txExpirationInMillis = txExpirationInMillis;
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
    public Request<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(9);

        requestParameters.put("code", code);
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
    public Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest req) {
        return new Request<>(yxChainApiService.getService().getRequiredKeys(req), yxChainApiService);
    }

    @Override
    public Request<PushedTransaction> pushTransaction(PackedTransaction req) {
        return new Request<>(yxChainApiService.getService().pushTransaction(req), yxChainApiService);
    }

    @Override
    public Request<List<String>> getPublicKeys() {
        return new Request<>(yxWalletApiService.getService().getPublicKeys(), yxWalletApiService);
    }

    @Override
    public Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainid) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(pubKeys);
        collectionReq.add(chainid);

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
    public Request<Actions> getActions(String accountName, Integer pos, Integer offset) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>(3);

        requestParameters.put("account_name", accountName);
        requestParameters.put("pos", pos);
        requestParameters.put("offset", offset);

        return new Request<>(yxHistoryApiService.getService().getActions(requestParameters), yxHistoryApiService);
    }

    @Override
    public Request<io.yosemite.data.remote.history.transaction.Transaction> getTransaction(String id) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("id", id);

        return new Request<>(yxHistoryApiService.getService().getTransaction(requestParameters), yxHistoryApiService);
    }

    @Override
    public Request<KeyAccounts> getKeyAccounts(String publicKey) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("public_key", publicKey);

        return new Request<>(yxHistoryApiService.getService().getKeyAccounts(requestParameters), yxHistoryApiService);
    }

    @Override
    public Request<ControlledAccounts> getControlledAccounts(String controllingAccountName) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("controlling_account", controllingAccountName);

        return new Request<>(yxHistoryApiService.getService().getControlledAccounts(requestParameters), yxHistoryApiService);
    }

    @Override
    public int getTxExpirationInMillis() {
        return txExpirationInMillis;
    }
}
