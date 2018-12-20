package io.yosemite.services;

import io.yosemite.data.remote.api.*;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.GetTableOptions;

import java.util.List;

public interface YosemiteApiRestClient {

    /* Chain */
    Request<Info> getInfo();

    Request<Block> getBlock(String blockNumberOrId);

    Request<Account> getAccount(String accountName);

    Request<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options);

    /**
     * Requests conversion from json-formatted parameter string to binary format
     * @param req request instance
     * @return json to binary result instance
     */
    Request<AbiJsonToBinResponse> abiJsonToBin(AbiJsonToBinRequest req);

    /**
     * Requests conversion from binary format to json-formatted parameter string 
     * @param req request instance
     * @return binary to json result instance
     */
    Request<AbiBinToJsonResponse> abiBinToJson(AbiBinToJsonRequest req);

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

    Request<Actions> getActions(String accountName, int startPosition, int offset);

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

    /**
     * Get the account name that pays the transaction fee
     *
     * @return fee payer account name
     */
    public String getDelegatedTransactionFeePayer();

    /**
     * Set the account name that pays the transaction fee.
     * Once this account is specified, all subsequent transactions should be provided signature of the fee payer account before being pushed to the blockchain.
     *
     * @param delegatedTransactionFeePayer fee payer account name
     */
    public void setDelegatedTransactionFeePayer(String delegatedTransactionFeePayer);
}
