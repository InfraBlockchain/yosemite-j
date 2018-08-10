package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.data.remote.model.chain.TableRow;
import io.yosemite.data.remote.model.history.action.GetTableOptions;
import io.yosemite.data.remote.model.types.TypeName;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.util.Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT;

/**
 * @author Eugene Chung
 */
public class YosemiteDigitalContractJ extends YosemiteJ {
    private final static int MAX_INPUT_STRING_LENGTH = 256;

    public YosemiteDigitalContractJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    public CompletableFuture<PushedTransaction> createDigitalContract(
            final String creator, final long sequence, final String digitalContractHash, final String additionalDocumentHash,
            final List<String> signers, final Date expiration, final short options, final String[] permissions) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (StringUtils.isEmpty(digitalContractHash)) throw new IllegalArgumentException("empty digitalContractHash");
        if (digitalContractHash.length() > MAX_INPUT_STRING_LENGTH) throw new IllegalArgumentException("too long digitalContractHash");
        if (additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) throw new IllegalArgumentException("too long additionalDocumentHash");
        if (signers == null || signers.isEmpty()) throw new IllegalArgumentException("empty signers");
        if (expiration == null) throw new IllegalArgumentException("wrong expiration");
        if (options < 0) throw new IllegalArgumentException("negative option");

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
        arrayObj.add(Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get().format(expiration));
        arrayObj.add(options);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "create", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> addSigners(
            final String creator, final long sequence, final List<String> signers, final String[] permissions) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (signers == null || signers.isEmpty()) throw new IllegalArgumentException("empty signers");

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
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (StringUtils.isEmpty(signer)) throw new IllegalArgumentException("empty signer");
        if (signerInfo != null && signerInfo.length() > MAX_INPUT_STRING_LENGTH) throw new IllegalArgumentException("too long signerInfo");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(signer);
        if (signerInfo != null) {
            arrayObj.add(signerInfo);
        }

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "sign", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{signer + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> updateAdditionalDocumentHash(
            final String creator, final long sequence, final String additionalDocumentHash, final String[] permissions) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (additionalDocumentHash != null && additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) {
            throw new IllegalArgumentException("too long additionalDocumentHash");
        }

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(additionalDocumentHash == null ? "" : additionalDocumentHash);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "upadddochash", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<PushedTransaction> removeDigitalContract(
            final String creator, final long sequence, final String[] permissions) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "remove", new Gson().toJson(arrayObj),
                isEmptyArray(permissions) ? new String[]{creator + "@active"} : permissions);
    }

    public CompletableFuture<TableRow> getCreatedDigitalContract(final String creator, final long sequence) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");

        GetTableOptions options = new GetTableOptions();
        options.setLowerBound(String.valueOf(sequence));
        options.setLimit(1);

        return getTableRows(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, creator, "dcontracts", options);
    }

    public CompletableFuture<TableRow> getSignerInfo(final String signer, final String creator, final long sequence) {
        if (StringUtils.isEmpty(signer)) throw new IllegalArgumentException("empty signer");
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");

        long creatorAsInteger = TypeName.string_to_name(creator);
        String dcIdSerializedHex = Utils.makeWebAssembly128BitIntegerAsHexString(creatorAsInteger, sequence);

        // cleos get table yx.dcontract user3 signers --index 2 --key-type i128 -L 0x0b000000000000007055729bdebaafc2 -l 1
        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("2"); // indicates secondary index 'dcids' of dcontract_signer_index
                                       // defined by contracts/yx.dcontract/yx.dcontract.hpp of YosemiteChain
        options.setKeyType("i128");
        options.setLowerBound(dcIdSerializedHex);
        options.setLimit(1);

        System.out.println(dcIdSerializedHex);

        return getTableRows(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, signer, "signers", options);
    }
}
