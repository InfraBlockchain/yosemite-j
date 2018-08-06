package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.model.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.model.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.model.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.model.chain.*;
import io.yosemite.data.remote.model.chain.TableRow;
import io.yosemite.data.remote.model.history.action.Actions;
import io.yosemite.data.remote.model.history.controlledaccounts.ControlledAccounts;
import io.yosemite.data.remote.model.history.keyaccounts.KeyAccounts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class YosemiteApiRestClientImpl implements YosemiteApiRestClient {

    private final YosemiteChainApiService yxChainApiService;
    private final YosemiteHistoryApiService yxHistoryApiService;
    private final YosemiteWalletApiService yxWalletApiService;

    YosemiteApiRestClientImpl(String baseUrl) {
        yxChainApiService = ApiServiceGenerator.createService(YosemiteChainApiService.class, baseUrl);
        yxWalletApiService = ApiServiceGenerator.createService(YosemiteWalletApiService.class, baseUrl);
        yxHistoryApiService = ApiServiceGenerator.createService(YosemiteHistoryApiService.class, baseUrl);
    }

    YosemiteApiRestClientImpl(String chainBaseUrl, String walletBaseUrl, String historyBaseUrl) {
        yxChainApiService = ApiServiceGenerator.createService(YosemiteChainApiService.class, chainBaseUrl);
        yxWalletApiService = ApiServiceGenerator.createService(YosemiteWalletApiService.class, walletBaseUrl);
        yxHistoryApiService = ApiServiceGenerator.createService(YosemiteHistoryApiService.class, historyBaseUrl);
    }

    @Override
    public Request<Info> getInfo() {
        return new Request<>(yxChainApiService.getInfo());
    }

    @Override
    public Request<Block> getBlock(String blockNumberorId) {
        return new Request<>(yxChainApiService.getBlock(blockNumberorId));
    }

    @Override
    public Request<TableRow> getTableRows(String scope, String code, String table) {
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(7);

        requestParameters.put("scope", scope);
        requestParameters.put("code", code);
        requestParameters.put("table", table);
        requestParameters.put("json", "true");

        return new Request<>(yxChainApiService.getTableRows(requestParameters));
    }

    @Override
    public Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req) {
        return new Request<>(yxChainApiService.abiJsonToBin(req));
    }

    @Override
    public Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest req) {
        return new Request<>(yxChainApiService.getRequiredKeys(req));
    }

    @Override
    public Request<PushedTransaction> pushTransaction(PackedTransaction req) {
        return new Request<>(yxChainApiService.pushTransaction(req));
    }

    @Override
    public Request<List<String>> getPublicKeys() {
        return new Request<>(yxWalletApiService.getPublicKeys());
    }

    @Override
    public Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainid) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(pubKeys);
        collectionReq.add(chainid);

        return new Request<>(yxWalletApiService.signTransaction(collectionReq));
    }

    @Override
    public Request<Actions> getActions(String accountName, Integer pos, Integer offset){
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>(3);

        requestParameters.put("account_name", accountName);
        requestParameters.put("pos", pos);
        requestParameters.put("offset", offset);

        return new Request<>(yxHistoryApiService.getActions(requestParameters));
    }

    @Override
    public Request<Transaction> getTransaction(String id){
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("id", id);

        return new Request<>(yxHistoryApiService.getTransaction(requestParameters));
    }

    @Override
    public Request<KeyAccounts> getKeyAccounts(String publicKey){
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("public_key", publicKey);

        return new Request<>(yxHistoryApiService.getKeyAccounts(requestParameters));
    }

    @Override
    public Request<ControlledAccounts> getControlledAccounts(String controllingAccountName){
        LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>(1);

        requestParameters.put("controlling_account", controllingAccountName);

        return new Request<>(yxHistoryApiService.getControlledAccounts(requestParameters));
    }
}
