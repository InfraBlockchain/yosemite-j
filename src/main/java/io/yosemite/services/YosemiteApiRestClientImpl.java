package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinReq;
import io.yosemite.data.remote.model.api.AbiJsonToBinRes;
import io.yosemite.data.remote.model.api.GetRequiredKeysReq;
import io.yosemite.data.remote.model.api.GetRequiredKeysRes;
import io.yosemite.data.remote.model.chain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class YosemiteApiRestClientImpl implements YosemiteApiRestClient {

    private final YosemiteChainApiService yosemiteChainApiService;
    private final YosemiteWalletApiService yosemiteWalletApiService;

    YosemiteApiRestClientImpl(String baseUrl) {
        yosemiteChainApiService = ApiServiceGenerator.createService(YosemiteChainApiService.class, baseUrl);
        yosemiteWalletApiService = ApiServiceGenerator.createService(YosemiteWalletApiService.class, baseUrl);
    }

    YosemiteApiRestClientImpl(String chainBaseUrl, String walletBaseUrl) {
        yosemiteChainApiService = ApiServiceGenerator.createService(YosemiteChainApiService.class, chainBaseUrl);
        yosemiteWalletApiService = ApiServiceGenerator.createService(YosemiteWalletApiService.class, walletBaseUrl);
    }

    @Override
    public Request<Info> getInfo() {
        return new Request<>(yosemiteChainApiService.getInfo());
    }

    @Override
    public Request<Block> getBlock(String blockNumberorId) {
        return new Request<>(yosemiteChainApiService.getBlock(blockNumberorId));
    }

    @Override
    public Request<AbiJsonToBinRes> abiJsonToBin(AbiJsonToBinReq req) {
        return new Request<>(yosemiteChainApiService.abiJsonToBin(req));
    }

    @Override
    public Request<GetRequiredKeysRes> getRequiredKeys(GetRequiredKeysReq req) {
        return new Request<>(yosemiteChainApiService.getRequiredKeys(req));
    }

    @Override
    public Request<PushedTransaction> pushTransaction(PackedTransaction req) {
        return new Request<>(yosemiteChainApiService.pushTransaction(req));
    }

    @Override
    public Request<List<String>> getPublicKeys() {
        return new Request<>(yosemiteWalletApiService.getPublicKeys());
    }

    @Override
    public Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainid) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(pubKeys);
        collectionReq.add(chainid);

        return new Request<>(yosemiteWalletApiService.signTransaction(collectionReq));
    }
}
