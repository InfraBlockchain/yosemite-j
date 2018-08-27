package io.yosemite.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.yosemite.crypto.digest.Sha256;
import io.yosemite.data.remote.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.util.GsonYosemiteTypeAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class YosemiteJ {

    private final static Logger logger = LoggerFactory.getLogger(YosemiteJ.class);

    private final YosemiteApiRestClient mYosemiteApiRestClient;

    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new GsonYosemiteTypeAdapterFactory())
            .excludeFieldsWithoutExposeAnnotation().create();

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

    private CompletableFuture<PackedTransaction> signAndPackTransaction(final SignedTransaction txnBeforeSign, final String chainId) {

        CompletableFuture<PackedTransaction> packedTxFuture;

        List<String> pubKeys = mYosemiteApiRestClient.getPublicKeys().execute();

        GetRequiredKeysRequest getRequiredKeysRequest = new GetRequiredKeysRequest(txnBeforeSign, pubKeys);

        packedTxFuture = mYosemiteApiRestClient.getRequiredKeys(getRequiredKeysRequest).executeAsync().thenApply(getRequiredKeysRes -> {
            SignedTransaction signedTx =
                    mYosemiteApiRestClient.signTransaction(txnBeforeSign, getRequiredKeysRes.getRequiredKeys(), chainId).execute();
            return new PackedTransaction(signedTx);
        });

        return packedTxFuture;
    }

    public final CompletableFuture<String> sign(byte[] dataTosign, String pubKey) {
        String sha256hex = Sha256.from(dataTosign).toString();

        return mYosemiteApiRestClient.signDigest(sha256hex, pubKey).executeAsync();
    }

    public final CompletableFuture<PushedTransaction> pushAction(
            final String contract, final String action, final String data, final String[] permissions) {

        logger.debug(contract);
        logger.debug(action);
        logger.debug(data);

        return getActionWithBinaryData(contract, action, data, permissions).thenCompose(actionReq ->
                mYosemiteApiRestClient.getInfo().executeAsync().thenCompose(info -> {

                    SignedTransaction txnBeforeSign = new SignedTransaction();

                    txnBeforeSign.addAction(actionReq);
                    txnBeforeSign.setReferenceBlock(info.getHeadBlockId());
                    txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(mYosemiteApiRestClient.getTxExpirationInMillis()));

                    return signAndPackTransaction(txnBeforeSign, info.getChainId()).thenCompose(packedTx -> mYosemiteApiRestClient.pushTransaction(packedTx).executeAsync());
                })
        );
    }

    public final CompletableFuture<TableRow> getTableRows(String code, String scope, String table, GetTableOptions options) {
        return mYosemiteApiRestClient.getTableRows(code, scope, table, options).executeAsync();
    }
}
