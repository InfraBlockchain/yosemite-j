package io.yosemiteblockchain.data.remote.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

public class AbiJsonToBinRequest {

    @Expose
    private String code;

    @Expose
    private String action;

    @Expose
    private JsonElement args;

    public AbiJsonToBinRequest(String code, String action, String args) {
        this.code = code;
        this.action = action;
        putArgs(args);
    }

    public JsonElement getArgs() {
        return args;
    }

    public void putArgs(String args) {
        this.args = new JsonParser().parse(args);
    }
}
