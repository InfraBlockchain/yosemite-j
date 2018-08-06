package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.model.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.model.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.model.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.model.chain.*;
import io.yosemite.data.remote.model.chain.TableRow;
import io.yosemite.data.remote.model.history.action.Actions;
import io.yosemite.data.remote.model.history.action.GetTableOptions;
import io.yosemite.data.remote.model.history.controlledaccounts.ControlledAccounts;
import io.yosemite.data.remote.model.history.keyaccounts.KeyAccounts;

import java.util.List;

public interface YosemiteApiRestClient {

    /* Chain */
    Request<Info> getInfo();

    Request<Block> getBlock(String blockNumberorId);

    Request<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options);

    Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req);

    Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest getRequiredKeysRequest);

    Request<PushedTransaction> pushTransaction(PackedTransaction req);

    /* Wallet */
    Request<List<String>> getPublicKeys();

    Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainId);

    /* History */
    Request<Actions> getActions(String accountName, Integer pos, Integer offset);

    Request<Transaction> getTransaction(String id);

    Request<KeyAccounts> getKeyAccounts(String publicKey);

    Request<ControlledAccounts> getControlledAccounts(String controllingAccountName);

}
