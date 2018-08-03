package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinReq;
import io.yosemite.data.remote.model.api.AbiJsonToBinRes;
import io.yosemite.data.remote.model.api.GetRequiredKeysReq;
import io.yosemite.data.remote.model.api.GetRequiredKeysRes;
import io.yosemite.data.remote.model.chain.*;

import java.util.List;

public interface YosemiteApiRestClient {

    /* Chain */
    Request<Info> getInfo();

    Request<Block> getBlock(String blockNumberorId);

    Request<AbiJsonToBinRes> abiJsonToBin(AbiJsonToBinReq req);

    Request<GetRequiredKeysRes> getRequiredKeys(GetRequiredKeysReq getRequiredKeysReq);

    Request<PushedTransaction> pushTransaction(PackedTransaction req);

    /* Wallet */
    Request<List<String>> getPublicKeys();

    Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainId);
}
