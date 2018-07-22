package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.*;
import org.yosemitex.data.remote.model.chain.*;

import java.util.List;

public interface EosApiRestClient {

    /* Chain */
    Info getInfo();

    Block getBlock(String blockNumberorId);

    AbiJsonToBinRes abiJsonToBin(AbiJsonToBinReq req);

    GetRequiredKeysRes getRequiredKeys(GetRequiredKeysReq getRequiredKeysReq);

    PushedTransaction pushTransaction(PackedTransaction req);

    /* Wallet */
    SignedTransaction signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainId);
}
