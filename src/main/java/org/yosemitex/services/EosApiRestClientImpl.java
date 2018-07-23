package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.*;
import org.yosemitex.data.remote.model.chain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EosApiRestClientImpl implements EosApiRestClient {

    private final EosChainApiService eosChainApiService;
    private final EosWalletApiService eosWalletApiService;

    public EosApiRestClientImpl(String baseUrl) {
        eosChainApiService = ApiServiceGenerator.createService(EosChainApiService.class, baseUrl);
        eosWalletApiService = ApiServiceGenerator.createService(EosWalletApiService.class, baseUrl);
    }

    @Override
    public Request<Info> getInfo() {
        return new Request<>(eosChainApiService.getInfo());
    }

    @Override
    public Request<Block> getBlock(String blockNumberorId) {
        return new Request<>(eosChainApiService.getBlock(blockNumberorId));
    }

    @Override
    public Request<AbiJsonToBinRes> abiJsonToBin(AbiJsonToBinReq req) {
        return new Request<>(eosChainApiService.abiJsonToBin(req));
    }

    @Override
    public Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainid) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(pubKeys);
        collectionReq.add(chainid);

        return new Request<>(eosWalletApiService.signTransaction(collectionReq));
    }

    @Override
    public Request<GetRequiredKeysRes> getRequiredKeys(GetRequiredKeysReq req) {
        return new Request<>(eosChainApiService.getRequiredKeys(req));
    }

    @Override
    public Request<PushedTransaction> pushTransaction(PackedTransaction req) {
        return new Request<>(eosChainApiService.pushTransaction(req));
    }
}
