package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.AbiJsonToBinReq;
import org.yosemitex.data.remote.model.api.AbiJsonToBinRes;
import org.yosemitex.data.remote.model.api.GetRequiredKeysReq;
import org.yosemitex.data.remote.model.api.GetRequiredKeysRes;
import org.yosemitex.data.remote.model.chain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class YxApiRestClientImpl implements YxApiRestClient {

    private final YxChainApiService yxChainApiService;
    private final YxWalletApiService yxWalletApiService;

    public YxApiRestClientImpl(String baseUrl) {
        yxChainApiService = ApiServiceGenerator.createService(YxChainApiService.class, baseUrl);
        yxWalletApiService = ApiServiceGenerator.createService(YxWalletApiService.class, baseUrl);
    }

    public YxApiRestClientImpl(String chainBaseUrl, String walletBaseUrl) {
        yxChainApiService = ApiServiceGenerator.createService(YxChainApiService.class, chainBaseUrl);
        yxWalletApiService = ApiServiceGenerator.createService(YxWalletApiService.class, walletBaseUrl);
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
    public Request<AbiJsonToBinRes> abiJsonToBin(AbiJsonToBinReq req) {
        return new Request<>(yxChainApiService.abiJsonToBin(req));
    }

    @Override
    public Request<GetRequiredKeysRes> getRequiredKeys(GetRequiredKeysReq req) {
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
}
