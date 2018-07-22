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
        eosChainApiService = EosApiServiceGenerator.createService(EosChainApiService.class, baseUrl);
        eosWalletApiService = EosApiServiceGenerator.createService(EosWalletApiService.class, baseUrl);
    }

    @Override
    public Info getInfo() {
        return EosApiServiceGenerator.executeSync(eosChainApiService.getInfo());
    }

    @Override
    public Block getBlock(String blockNumberorId) {
        return EosApiServiceGenerator.executeSync(eosChainApiService.getBlock(blockNumberorId));
    }

    @Override
    public AbiJsonToBinRes abiJsonToBin(AbiJsonToBinReq req) {
        return EosApiServiceGenerator.executeSync(eosChainApiService.abiJsonToBin(req));
    }

    @Override
    public SignedTransaction signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainid) {
        Collection collectionReq = new ArrayList();
        collectionReq.add(transactionToSign);
        collectionReq.add(pubKeys);
        collectionReq.add(chainid);

        return EosApiServiceGenerator.executeSync(eosWalletApiService.signTransaction(collectionReq));
    }

    @Override
    public GetRequiredKeysRes getRequiredKeys(GetRequiredKeysReq req) {
        return EosApiServiceGenerator.executeSync(eosChainApiService.getRequiredKeys(req));
    }

    @Override
    public PushedTransaction pushTransaction(PackedTransaction req) {
        return EosApiServiceGenerator.executeSync(eosChainApiService.pushTransaction(req));
    }
}
