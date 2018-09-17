package io.yosemite.services;

import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.AbiJsonToBinResponse;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.api.GetRequiredKeysResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.GetTableOptions;

import java.util.List;

public interface YosemiteApiRestClient {

    /* Chain */
    Request<Info> getInfo();

    Request<Block> getBlock(String blockNumberOrId);

    Request<Account> getAccount(String accountName);

    Request<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options);

    Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req);

    Request<GetRequiredKeysResponse> getRequiredKeys(GetRequiredKeysRequest getRequiredKeysRequest);

    Request<PushedTransaction> pushTransaction(PackedTransaction req);

    /* Wallet */
    Request<String> createKey();

    Request<String> createKey(String walletName);

    Request<String> createKey(String walletName, String keyType);

    Request<List<String>> getPublicKeys();

    Request<SignedTransaction> signTransaction(SignedTransaction transactionToSign, List<String> pubKeys, String chainId);

    Request<String> signDigest(String hexData, String pubKey);

    /* History */
    Request<io.yosemite.data.remote.history.transaction.Transaction> getTransaction(String id);

    int getTxExpirationInMillis();

    /**
     * Set the transaction expiration time
     *
     * @param txExpirationInMillis expiration time in milliseconds
     */
    void setTxExpirationInMillis(int txExpirationInMillis);

    String getTransactionVoteTarget();

    /**
     * Set the transaction vote target account for PoT
     *
     * @param transactionVoteTarget The account name to vote to
     */
    void setTransactionVoteTarget(String transactionVoteTarget);
}
