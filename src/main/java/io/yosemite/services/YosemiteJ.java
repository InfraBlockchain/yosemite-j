package io.yosemite.services;

import com.google.gson.Gson;
import io.yosemite.crypto.digest.Sha256;
import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.util.Utils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class YosemiteJ {

    private final YosemiteApiRestClient mYosemiteApiRestClient;

    protected static final Gson gson = Utils.createYosemiteJGsonBuilder().create();

    protected YosemiteJ(YosemiteApiRestClient yosemiteApiRestClient) {
        mYosemiteApiRestClient = yosemiteApiRestClient;
    }

    protected final boolean isEmptyArray(String[] array) {
        return array == null || array.length == 0;
    }

    private CompletableFuture<Action> getActionWithBinaryData(String contract, String action, String data, String[] permissions) {
        AbiJsonToBinRequest abiJsonToBinRequest = new AbiJsonToBinRequest(contract, action, data);

        return mYosemiteApiRestClient.abiJsonToBin(abiJsonToBinRequest).executeAsync().thenApply(abiJsonToBinRes -> {
            Action actionReq = new Action(contract, action);
            actionReq.setAuthorization(permissions);
            actionReq.setData(abiJsonToBinRes.getBinargs());

            return actionReq;
        });
    }

    private CompletableFuture<PackedTransaction> signAndPackTransaction(final SignedTransaction txnBeforeSign,
                                                                        final String chainId,
                                                                        String[] requiredPublicKeys) {
        if (requiredPublicKeys == null || requiredPublicKeys.length == 0) {
            List<String> pubKeys = mYosemiteApiRestClient.getPublicKeys().execute();
            GetRequiredKeysRequest getRequiredKeysRequest = new GetRequiredKeysRequest(txnBeforeSign, pubKeys);

            return mYosemiteApiRestClient.getRequiredKeys(getRequiredKeysRequest).executeAsync().thenApply(getRequiredKeysRes -> {
                SignedTransaction signedTx =
                        mYosemiteApiRestClient.signTransaction(txnBeforeSign, getRequiredKeysRes.getRequiredKeys(), chainId).execute();
                return new PackedTransaction(signedTx);
            });
        }

        return mYosemiteApiRestClient.signTransaction(txnBeforeSign, Arrays.asList(requiredPublicKeys), chainId).executeAsync().thenApply(
                PackedTransaction::new
        );
    }

    /**
     * Make digital signature of arbitrary data by the wallet daemon.
     * @param dataToSign arbitrary data
     * @param publicKey the public key to find the matching private key
     * @return CompletableFuture instance to get the digital signature result
     */
    public final CompletableFuture<String> sign(byte[] dataToSign, String publicKey) {
        String sha256hex = Sha256.from(dataToSign).toString();

        return mYosemiteApiRestClient.signDigest(sha256hex, publicKey).executeAsync();
    }

    /**
     * Push an action to the Yosemite chain network.
     * @param contract the name of the smart contract
     * @param action the name of the action method
     * @param data the json formatted data
     * @param permissions the required permission arrays of the action
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public final CompletableFuture<PushedTransaction> pushAction(
            final String contract, final String action, final String data, final String[] permissions) {
        return pushAction(contract, action, data, permissions, null);
    }

    /**
     * Push an action to the Yosemite chain network.
     * @param contract the name of the smart contract
     * @param action the name of the action method
     * @param data the json formatted data
     * @param permissions the required permission arrays of the action
     * @param requiredPublicKeys the required public keys matching to permissions; if not provided, performance problem would be occurred
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public final CompletableFuture<PushedTransaction> pushAction(
            final String contract, final String action, final String data,
            final String[] permissions, @Nullable final String[] requiredPublicKeys) {

        return getActionWithBinaryData(contract, action, data, permissions).thenCompose(actionReq ->
                mYosemiteApiRestClient.getInfo().executeAsync().thenCompose(info -> {

                    SignedTransaction txnBeforeSign = new SignedTransaction();

                    txnBeforeSign.addAction(actionReq);
                    txnBeforeSign.setReferenceBlock(info.getHeadBlockId());
                    txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(mYosemiteApiRestClient.getTxExpirationInMillis()));
                    txnBeforeSign.setTransactionVoteTarget(mYosemiteApiRestClient.getTransactionVoteTarget());

                    return signAndPackTransaction(txnBeforeSign, info.getChainId(), requiredPublicKeys).thenCompose(packedTx ->
                            mYosemiteApiRestClient.pushTransaction(packedTx).executeAsync(txnBeforeSign));
                })
        );
    }

    /**
     * Get the table information of the smart contract from the RAM database
     * @param contract the name of the smart contract
     * @param scope the scope within the contract in which the table is found
     * @param table the name of the table as specified by the contract abi
     * @param options parameters of this operation
     * @return CompletableFuture instance to get TableRow instance
     */
    public final CompletableFuture<TableRow> getTableRows(String contract, String scope, String table, GetTableOptions options) {
        return mYosemiteApiRestClient.getTableRows(contract, scope, table, options).executeAsync();
    }
}
