package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.AbiJsonToBinReq;
import org.yosemitex.data.remote.model.api.AbiJsonToBinRes;
import org.yosemitex.data.remote.model.api.GetRequiredKeysReq;
import org.yosemitex.data.remote.model.api.GetRequiredKeysRes;
import org.yosemitex.data.remote.model.chain.*;

import java.util.List;

public interface EosApiRestClient {

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
