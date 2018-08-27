package io.yosemite.services;

import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.remote.history.controlledaccounts.ControlledAccounts;
import io.yosemite.data.remote.history.keyaccounts.KeyAccounts;

import java.util.List;

public interface YosemiteApiRestClient {

    /* Chain */
    Request<Info> getInfo();

    Request<Block> getBlock(String blockNumberOrId);

    Request<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options);

    Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req);

    Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest getRequiredKeysRequest);

    Request<PushedTransaction> pushTransaction(PackedTransaction req);

    /* Wallet */
    Request<List<String>> getPublicKeys();

    Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainId);

    Request<String> signDigest(String data, String pubKey);

    /* History */
    Request<Actions> getActions(String accountName, Integer pos, Integer offset);

    Request<io.yosemite.data.remote.history.transaction.Transaction> getTransaction(String id);

    Request<KeyAccounts> getKeyAccounts(String publicKey);

    Request<ControlledAccounts> getControlledAccounts(String controllingAccountName);

    int getTxExpirationInMillis();
}
