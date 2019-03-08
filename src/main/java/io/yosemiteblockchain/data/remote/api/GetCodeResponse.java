package io.yosemiteblockchain.data.remote.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import io.yosemiteblockchain.crypto.digest.Sha256;
import io.yosemiteblockchain.util.StringUtils;


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
