package io.yosemite.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.model.api.AbiJsonToBinReq;
import io.yosemite.data.remote.model.api.GetRequiredKeysReq;
import io.yosemite.data.remote.model.chain.Action;
import io.yosemite.data.remote.model.chain.PackedTransaction;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.data.remote.model.chain.SignedTransaction;
import io.yosemite.data.remote.model.types.TypeAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static io.yosemite.util.Consts.YX_NATIVE_TOKEN_CONTRACT;

public class YosemiteJ {

    final static Logger logger = LoggerFactory.getLogger(YosemiteJ.class);

    public final YosemiteApiRestClient mYosemiteApiRestClient;

    public YosemiteJ(YosemiteApiRestClient yosemiteApiRestClient) {
        mYosemiteApiRestClient = yosemiteApiRestClient;
    }

    private CompletableFuture<Action> getActionWithBinaryData(String contract, String action, String data, String[] permissions) {


        AbiJsonToBinReq abiJsonToBinReq = new AbiJsonToBinReq(contract, action, data);

        return mYosemiteApiRestClient.abiJsonToBin(abiJsonToBinReq).executeAsync().thenApply(abiJsonToBinRes -> {

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

            GetRequiredKeysReq getRequiredKeysReq = new GetRequiredKeysReq(txnBeforeSign, pubKeys);

            packedTxFuture = mYosemiteApiRestClient.getRequiredKeys(getRequiredKeysReq).executeAsync().thenApply(getRequiredKeysRes -> {

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

    public CompletableFuture<PushedTransaction> pushAction(final String contract, final String action, final String data, final String[] permissions) {

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
