package org.yosemitex.data.remote.model.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.yosemitex.crypto.digest.Sha256;
import org.yosemitex.util.StringUtils;


public class GetCodeResponse {
    @Expose
    private String account_name;

    @Expose
    private String wast;

    @Expose
    private String code_hash;

    @Expose
    private JsonObject abi;

    public JsonObject getAbi() {
        return abi;
    }

    public boolean isValidCode() {
        return !(StringUtils.isEmpty(code_hash) || Sha256.ZERO_HASH.toString().equals(code_hash));
    }
}
