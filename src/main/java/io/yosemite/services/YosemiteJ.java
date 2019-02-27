package io.yosemite.services;

import com.google.gson.Gson;
import io.yosemite.services.yxcontracts.StandardTokenConsts;
import io.yosemite.crypto.digest.Sha256;
import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.data.types.TypePermission;
import io.yosemite.exception.YosemiteApiException;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static io.yosemite.Consts.YOSEMITE_STANDARD_TOKEN_ABI_CONTRACT;
import static java.util.stream.Collectors.toList;

public abstract class YosemiteJ {

    private final YosemiteApiRestClient mYosemiteApiRestClient;

    protected static final Gson gson = Utils.createYosemiteJGson();

    protected YosemiteJ(YosemiteApiRestClient yosemiteApiRestClient) {
        mYosemiteApiRestClient = yosemiteApiRestClient;
    }

    private CompletableFuture<Action> getActionWithBinaryData(final String contract, String actionName, String data,
                                                              Collection<TypePermission> permissions) {
        String abiTarget = contract;
        if (StandardTokenConsts.STANDARD_TOKEN_ACTIONS.contains(actionName)) {
            abiTarget = YOSEMITE_STANDARD_TOKEN_ABI_CONTRACT;
        }
        AbiJsonToBinRequest abiJsonToBinRequest = new AbiJsonToBinRequest(abiTarget, actionName, data);

        return mYosemiteApiRestClient.abiJsonToBin(abiJsonToBinRequest).executeAsync().thenApply(abiJsonToBinRes -> {
            Action action = new Action(contract, actionName);
            action.setAuthorization(permissions);
            action.setData(abiJsonToBinRes.getBinargs());

            return action;
        });
    }

    private CompletableFuture<PackedTransaction> signAndPackTransaction(final SignedTransaction txnBeforeSign,
                                                                        final String chainId,
                                                                        List<String> requiredPublicKeys) {
        return signTransaction(txnBeforeSign, chainId, requiredPublicKeys).thenApply(
                PackedTransaction::new
        );
    }

    /**
     * Get signed transaction.
     *
     * You may use the returned signed transaction as the intermediate data before sending it to the network
     * @param txnBeforeSign transaction to sign
     * @param chainId blockchain ID to send this transaction
     * @param requiredPublicKeys Keys that are going to be used to sign this transaction
     * @return CompletableFuture instance that contains the original transaction data and its signature added
     */
    public CompletableFuture<SignedTransaction> signTransaction(SignedTransaction txnBeforeSign,
                                                                String chainId,
                                                                List<String> requiredPublicKeys) {
        if (requiredPublicKeys == null || requiredPublicKeys.isEmpty()) {
            List<String> pubKeys = mYosemiteApiRestClient.getPublicKeys().execute();
            GetRequiredKeysRequest getRequiredKeysRequest = new GetRequiredKeysRequest(txnBeforeSign, pubKeys);

            return mYosemiteApiRestClient.getRequiredKeys(getRequiredKeysRequest).executeAsync().thenApply(
                    keysResult ->
                        mYosemiteApiRestClient.signTransaction(txnBeforeSign, keysResult.getRequiredKeys(), chainId).execute());
        }

        return mYosemiteApiRestClient.signTransaction(txnBeforeSign, requiredPublicKeys, chainId).executeAsync();
    }

    /**
     * Get signed transaction.
     * This method is used only once at its signing pipeline operations
     * Use {@link #signAndPackTransaction(SignedTransaction, String, List)}} for the subsequent operations.
     *
     * You may use the returned signed transaction as the intermediate data before sending it to the network
     * @param contract the name of the smart contract
     * @param actionName the name of the action method
     * @param data the json formatted data
     * @param params common parameters
     * @return CompletableFuture instance that contains the original transaction data and its signature added
     */
    public CompletableFuture<SignedTransaction> signTransaction(final String contract, final String actionName, final String data,
                                                                final TransactionParameters params) {
        if (params == null) throw new IllegalArgumentException("params cannot be null");

        return getActionWithBinaryData(contract, actionName, data, params.getPermissions()).thenCompose(action ->
            mYosemiteApiRestClient.getInfo().executeAsync().thenCompose(info -> {

                SignedTransaction txnBeforeSign = buildSignedTransaction(action, info, params);

                return signTransaction(txnBeforeSign, info.getChainId(), params.getPublicKeys());
            })
        );
    }

    private SignedTransaction buildSignedTransaction(Action action, Info info, TransactionParameters params) {
        return buildSignedTransaction(Stream.of(action).collect(toList()), info, params);
    }

    private SignedTransaction buildSignedTransaction(List<Action> actions, Info info, TransactionParameters params) {
        String txFeePayer = params.getTransactionFeePayer() != null ?
                params.getTransactionFeePayer() : mYosemiteApiRestClient.getTransactionFeePayer();
        if (StringUtils.isEmpty(txFeePayer)) {
            throw new YosemiteApiException("transaction fee payer must be set");
        }

        SignedTransaction txnBeforeSign = new SignedTransaction();

        txnBeforeSign.addActions(actions);
        txnBeforeSign.setReferenceBlock(info.getHeadBlockId());
        txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(
                params.getTxExpirationInMillis() >= 0 ? params.getTxExpirationInMillis() : mYosemiteApiRestClient.getTxExpirationInMillis()));
        txnBeforeSign.setStringTransactionExtension(TransactionExtensionField.TRANSACTION_VOTE_ACCOUNT,
                params.getTransactionVoteTarget() != null ? params.getTransactionVoteTarget() : mYosemiteApiRestClient.getTransactionVoteTarget());
        txnBeforeSign.setStringTransactionExtension(TransactionExtensionField.TRANSACTION_FEE_PAYER, txFeePayer);
        return txnBeforeSign;
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
     * Note that the transaction fee payer must be set by TransactionParameters or YosemiteApiRestClient.
     * @param contract the name of the smart contract
     * @param actionName the name of the action
     * @param data the json-formatted data
     * @param params common parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public final CompletableFuture<PushedTransaction> pushAction(
            final String contract, final String actionName, final String data, final TransactionParameters params) {
        if (params == null) throw new IllegalArgumentException("params cannot be null");

        return getActionWithBinaryData(contract, actionName, data, params.getPermissions()).thenCompose(action ->
            mYosemiteApiRestClient.getInfo().executeAsync().thenCompose(info -> {
                SignedTransaction txnBeforeSign = buildSignedTransaction(action, info, params);
                return signAndPackTransaction(txnBeforeSign, info.getChainId(), params.getPublicKeys()).thenCompose(packedTx ->
                        mYosemiteApiRestClient.pushTransaction(packedTx).executeAsync(txnBeforeSign));
            })
        );
    }

    /**
     * Push multi actions as the same transaction to the Yosemite chain network.
     * All actions must have the same transaction parameters.
     * Note that the transaction fee payer must be set by TransactionParameters or YosemiteApiRestClient.
     * @param actions the triplet of contract and its action name and json-formatted data
     * @param params common parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public final CompletableFuture<PushedTransaction> pushActions(
        Collection<ActionSpecifier> actions, final TransactionParameters params) {
        if (params == null) throw new IllegalArgumentException("params cannot be null");
        if (actions == null || actions.isEmpty()) throw new IllegalArgumentException("actions cannot be null");

        ArrayList<CompletableFuture<Action>> futures = new ArrayList<>();
        for (ActionSpecifier actionSpecifier : actions) {
            Collection<TypePermission> actionPermissions = actionSpecifier.getPermissions();
            CompletableFuture<Action> actionWithBinaryDataFuture =
                getActionWithBinaryData(actionSpecifier.getValue0(), actionSpecifier.getValue1(), actionSpecifier.getValue2(),
                    actionPermissions.isEmpty() ? params.getPermissions() : actionPermissions);
            futures.add(actionWithBinaryDataFuture);
        }

        List<Action> actionList = Stream.of(futures.toArray(new CompletableFuture[0])).map(CompletableFuture<Action>::join).collect(toList());
        return mYosemiteApiRestClient.getInfo().executeAsync().thenCompose(info -> {
            SignedTransaction txnBeforeSign = buildSignedTransaction(actionList, info, params);
            return signAndPackTransaction(txnBeforeSign, info.getChainId(), params.getPublicKeys()).thenCompose(packedTx ->
                mYosemiteApiRestClient.pushTransaction(packedTx).executeAsync(txnBeforeSign));
        });
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

    protected TransactionParameters buildCommonParametersWithDefaults(TransactionParameters transactionParameters,
                                                                      String defaultActorAccount) {
        if (transactionParameters == null) {
            TransactionParameters.TransactionParametersBuilder txParametersBuilder =
                    TransactionParameters.Builder().addPermission(defaultActorAccount);
            if (StringUtils.isEmpty(mYosemiteApiRestClient.getTransactionFeePayer())) {
                txParametersBuilder = txParametersBuilder.setTransactionFeePayer(defaultActorAccount);
            }
            return txParametersBuilder.build();
        }

        List<TypePermission> permissions = transactionParameters.getPermissions();
        if (permissions.isEmpty()) {
            permissions.add(new TypePermission(defaultActorAccount));
        }

        // set transaction fee payer as default actor if it's not set
        if (StringUtils.isEmpty(transactionParameters.getTransactionFeePayer())
            && StringUtils.isEmpty(mYosemiteApiRestClient.getTransactionFeePayer())) {

            transactionParameters.setTransactionFeePayer(defaultActorAccount);
        }

        return transactionParameters;
    }

    /**
     * Get the information of one of standard tokens.
     * @param token token account name
     * @return CompletableFuture instance to get TokenInfo instance
     */
    public CompletableFuture<TokenInfo> getTokenInfo(String token) {
        if (StringUtils.isEmpty(token)) throw new IllegalArgumentException("wrong token");
        return mYosemiteApiRestClient.getTokenInfo(token).executeAsync();
    }

    /**
     * Get the balance of the account for one of standard tokens.
     * @param token token account name
     * @param account target account name
     * @return CompletableFuture instance to get TypeAsset instance
     */
    public CompletableFuture<TypeAsset> getAccountBalance(String token, String account) {
        if (StringUtils.isEmpty(token)) throw new IllegalArgumentException("wrong token");
        if (StringUtils.isEmpty(account)) throw new IllegalArgumentException("wrong account");
        return mYosemiteApiRestClient.getTokenBalance(token, account).executeAsync();
    }
}
