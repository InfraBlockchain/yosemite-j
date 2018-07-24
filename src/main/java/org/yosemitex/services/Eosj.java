package org.yosemitex.services;

import org.yosemitex.data.remote.model.api.AbiJsonToBinReq;
import org.yosemitex.data.remote.model.api.GetRequiredKeysReq;
import org.yosemitex.data.remote.model.chain.Action;
import org.yosemitex.data.remote.model.chain.PackedTransaction;
import org.yosemitex.data.remote.model.chain.PushedTransaction;
import org.yosemitex.data.remote.model.chain.SignedTransaction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Eosj {

    public final EosApiRestClient mEosApiRestClient;

    public Eosj(EosApiRestClient eosApiRestClient) {
        mEosApiRestClient = eosApiRestClient;
    }

    private CompletableFuture<Action> getActionWithBinaryData(String contract, String action, String data, String[] permissions) {

        AbiJsonToBinReq abiJsonToBinReq = new AbiJsonToBinReq(contract, action, data);

        return mEosApiRestClient.abiJsonToBin(abiJsonToBinReq).executeAsync().thenApply(abiJsonToBinRes -> {

            Action actionReq = new Action(contract, action);
            actionReq.setAuthorization(permissions);
            actionReq.setData(abiJsonToBinRes.getBinargs());

            return actionReq;
        });
    }

    private CompletableFuture<PackedTransaction> signAndPackTransaction(final SignedTransaction txnBeforeSign, final String chainId) {

        CompletableFuture<PackedTransaction> packedTxFuture;

        try {
            List<String> pubKeys = mEosApiRestClient.getPublicKeys().execute();

            GetRequiredKeysReq getRequiredKeysReq = new GetRequiredKeysReq(txnBeforeSign, pubKeys);

            packedTxFuture = mEosApiRestClient.getRequiredKeys(getRequiredKeysReq).executeAsync().thenApply(getRequiredKeysRes -> {

                SignedTransaction signedTx;

                try {
                    signedTx = mEosApiRestClient.signTransaction(txnBeforeSign, getRequiredKeysRes.getRequiredKeys(), chainId).execute();
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

    public CompletableFuture<PushedTransaction> pushAction(final String contract, final String action, final String data, final String[] permissions) {

        return getActionWithBinaryData(contract, action, data, permissions).thenCompose(actionReq ->
                mEosApiRestClient.getInfo().executeAsync().thenCompose(info -> {

                    SignedTransaction txnBeforeSign = new SignedTransaction();

                    txnBeforeSign.addAction(actionReq);
                    txnBeforeSign.setReferenceBlock(info.getHeadBlockId());
                    txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(3000));

                    return signAndPackTransaction(txnBeforeSign, info.getChainId()).thenCompose(packedTx -> mEosApiRestClient.pushTransaction(packedTx).executeAsync());
                })
        );
    }
}
