package io.yosemite.services;

import io.yosemite.data.remote.model.api.AbiJsonToBinRequest;
import io.yosemite.data.remote.model.api.GetRequiredKeysRequest;
import io.yosemite.data.remote.model.chain.Action;
import io.yosemite.data.remote.model.chain.PackedTransaction;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.data.remote.model.chain.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class YosemiteJ {

    private final static Logger logger = LoggerFactory.getLogger(YosemiteJ.class);

    private final YosemiteApiRestClient mYosemiteApiRestClient;

    protected YosemiteJ(YosemiteApiRestClient yosemiteApiRestClient) {
        mYosemiteApiRestClient = yosemiteApiRestClient;
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

        try {
            List<String> pubKeys = mYosemiteApiRestClient.getPublicKeys().execute();

            GetRequiredKeysRequest getRequiredKeysRequest = new GetRequiredKeysRequest(txnBeforeSign, pubKeys);

            packedTxFuture = mYosemiteApiRestClient.getRequiredKeys(getRequiredKeysRequest).executeAsync().thenApply(getRequiredKeysRes -> {

                SignedTransaction signedTx;

                try {
                    signedTx = mYosemiteApiRestClient.signTransaction(txnBeforeSign, getRequiredKeysRes.getRequiredKeys(), chainId).execute();
                } catch (IOException e) {
                    throw new CompletionException(e);
                }

                return new PackedTransaction(signedTx);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packedTxFuture;
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
                    txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(3000));

                    return signAndPackTransaction(txnBeforeSign, info.getChainId()).thenCompose(packedTx -> mYosemiteApiRestClient.pushTransaction(packedTx).executeAsync());
                })
        );
    }
}
