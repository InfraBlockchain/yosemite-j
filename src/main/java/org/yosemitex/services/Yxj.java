package org.yosemitex.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yosemitex.data.remote.model.api.AbiJsonToBinReq;
import org.yosemitex.data.remote.model.api.GetRequiredKeysReq;
import org.yosemitex.data.remote.model.chain.Action;
import org.yosemitex.data.remote.model.chain.PackedTransaction;
import org.yosemitex.data.remote.model.chain.PushedTransaction;
import org.yosemitex.data.remote.model.chain.SignedTransaction;
import org.yosemitex.data.remote.model.types.TypeAsset;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.yosemitex.util.Consts.YX_NATIVE_TOKEN_CONTRACT;

public class Yxj {

    final static Logger logger = LoggerFactory.getLogger(Yxj.class);

    public final YxApiRestClient mYxApiRestClient;

    public Yxj(YxApiRestClient yxApiRestClient) {
        mYxApiRestClient = yxApiRestClient;
    }

    private CompletableFuture<Action> getActionWithBinaryData(String contract, String action, String data, String[] permissions) {


        AbiJsonToBinReq abiJsonToBinReq = new AbiJsonToBinReq(contract, action, data);

        return mYxApiRestClient.abiJsonToBin(abiJsonToBinReq).executeAsync().thenApply(abiJsonToBinRes -> {

            Action actionReq = new Action(contract, action);
            actionReq.setAuthorization(permissions);
            actionReq.setData(abiJsonToBinRes.getBinargs());

            return actionReq;
        });
    }

    private CompletableFuture<PackedTransaction> signAndPackTransaction(final SignedTransaction txnBeforeSign, final String chainId) {

        CompletableFuture<PackedTransaction> packedTxFuture;

        try {
            List<String> pubKeys = mYxApiRestClient.getPublicKeys().execute();

            GetRequiredKeysReq getRequiredKeysReq = new GetRequiredKeysReq(txnBeforeSign, pubKeys);

            packedTxFuture = mYxApiRestClient.getRequiredKeys(getRequiredKeysReq).executeAsync().thenApply(getRequiredKeysRes -> {

                SignedTransaction signedTx;

                try {
                    signedTx = mYxApiRestClient.signTransaction(txnBeforeSign, getRequiredKeysRes.getRequiredKeys(), chainId).execute();
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

        logger.debug(contract);
        logger.debug(action);
        logger.debug(data);

        return getActionWithBinaryData(contract, action, data, permissions).thenCompose(actionReq ->
                mYxApiRestClient.getInfo().executeAsync().thenCompose(info -> {

                    SignedTransaction txnBeforeSign = new SignedTransaction();

                    txnBeforeSign.addAction(actionReq);
                    txnBeforeSign.setReferenceBlock(info.getHeadBlockId());
                    txnBeforeSign.setExpiration(info.getTimeAfterHeadBlockTime(3000));

                    return signAndPackTransaction(txnBeforeSign, info.getChainId()).thenCompose(packedTx -> mYxApiRestClient.pushTransaction(packedTx).executeAsync());
                })
        );
    }

    public CompletableFuture<PushedTransaction> issueNativeToken(final String to, final String quantity, final String issuer, final String memo, final String[] permissions) {

        JsonArray arrayObj = new JsonArray();
        arrayObj.add(to);
        JsonObject quantityObj = new JsonObject();
        quantityObj.addProperty("quantity", new TypeAsset(quantity).toString());
        quantityObj.addProperty("issuer", issuer);
        arrayObj.add(quantityObj);
        arrayObj.add(memo);


        return pushAction(YX_NATIVE_TOKEN_CONTRACT, "issuen", new Gson().toJson(arrayObj), permissions);
    }

    public CompletableFuture<PushedTransaction> redeemNativeToken(final String quantity, final String issuer, final String memo, final String[] permissions) {

        JsonObject object = new JsonObject();
        JsonObject quantityObj = new JsonObject();
        quantityObj.addProperty("quantity", new TypeAsset(quantity).toString());
        quantityObj.addProperty("issuer", issuer);
        object.add("quantity", quantityObj);
        object.addProperty("memo", memo);

        return pushAction(YX_NATIVE_TOKEN_CONTRACT, "redeemn", new Gson().toJson(object), permissions);
    }

    public CompletableFuture<PushedTransaction> transferNativeToken(final String from, final String to, final String quantity, final String issuer, final String payer, final String memo, final String[] permissions) {

        JsonObject object = new JsonObject();
        object.addProperty("from", from);
        object.addProperty("to", to);

        String action;

        if (issuer.isEmpty()) {
            action = "transfer";
            object.addProperty("quantity", new TypeAsset(quantity).toString());
        } else {
            action = "transfern";
            JsonObject quantityObj = new JsonObject();
            quantityObj.addProperty("quantity", new TypeAsset(quantity).toString());
            quantityObj.addProperty("issuer", issuer);
            object.add("quantity", quantityObj);
        }

        object.addProperty("payer", payer);
        object.addProperty("memo", memo);

        return pushAction(YX_NATIVE_TOKEN_CONTRACT, action, new Gson().toJson(object), permissions);
    }
}
