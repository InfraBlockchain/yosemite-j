package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT;

/**
 * @author Eugene Chung
 */
public class YosemiteDigitalContractJ extends YosemiteJ {
    private final static ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf;
            });

    private final static int MAX_INPUT_STRING_LENGTH = 256;

    public YosemiteDigitalContractJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    private boolean isEmptyArray(String[] array) {
        return array == null || array.length == 0;
    }

    public CompletableFuture<PushedTransaction> createDigitalContract(
            final String creator, final long sequence, final String digitalContractHash, final String additionalDocumentHash,
            final List<String> signers, final Date expiration, final short options, final String[] permissions) {
        if (creator.isEmpty()) throw new InvalidParameterException("empty creator");
        if (sequence < 0) throw new InvalidParameterException("negative sequence");
        if (digitalContractHash.isEmpty()) throw new InvalidParameterException("empty digitalContractHash");
        if (digitalContractHash.length() > MAX_INPUT_STRING_LENGTH) throw new InvalidParameterException("too long digitalContractHash");
        if (additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) throw new InvalidParameterException("too long additionalDocumentHash");
        if (signers.isEmpty()) throw new InvalidParameterException("empty signers");
        if (options < 0) throw new InvalidParameterException("negative option");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(digitalContractHash);
        arrayObj.add(additionalDocumentHash);
        JsonArray signersObj = new JsonArray();
        for (String signer : signers) {
            signersObj.add(signer);
        }
        arrayObj.add(signersObj);
        arrayObj.add(SIMPLE_DATE_FORMAT_THREAD_LOCAL.get().format(expiration));
        arrayObj.add(options);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "create", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> addSigners(
            final String creator, final long sequence, final List<String> signers, final String[] permissions) {
        if (creator.isEmpty()) throw new InvalidParameterException("empty creator");
        if (sequence < 0) throw new InvalidParameterException("negative sequence");
        if (signers.isEmpty()) throw new InvalidParameterException("empty signers");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        JsonArray signersObj = new JsonArray();
        for (String signer : signers) {
            signersObj.add(signer);
        }
        arrayObj.add(signersObj);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "addsigners", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> signDigitalDocument(
            final String creator, final long sequence, final String signer, final String signerInfo, final String[] permissions) {
        if (creator.isEmpty()) throw new InvalidParameterException("empty creator");
        if (sequence < 0) throw new InvalidParameterException("negative sequence");
        if (signer.isEmpty()) throw new InvalidParameterException("empty signer");
        if (signerInfo.length() > MAX_INPUT_STRING_LENGTH) throw new InvalidParameterException("too long signerInfo");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(signer);
        arrayObj.add(signerInfo);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "sign", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{signer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> updateAdditionalDocumentHash(
            final String creator, final long sequence, final String additionalDocumentHash, final String[] permissions) {
        if (creator.isEmpty()) throw new InvalidParameterException("empty creator");
        if (sequence < 0) throw new InvalidParameterException("negative sequence");
        if (additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) throw new InvalidParameterException("too long additionalDocumentHash");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(additionalDocumentHash);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "upadddochash", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> removeDigitalContract(
            final String creator, final long sequence, final String[] permissions) {
        if (creator.isEmpty()) throw new InvalidParameterException("empty creator");
        if (sequence < 0) throw new InvalidParameterException("negative sequence");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "remove", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }
}
