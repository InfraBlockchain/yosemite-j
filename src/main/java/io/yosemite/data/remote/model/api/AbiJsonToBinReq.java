package io.yosemite.data.remote.model.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

public class AbiJsonToBinReq {

    @Expose
    private String code;

    @Expose
    private String action;

    @Expose
    private JsonElement args;

    public AbiJsonToBinReq(String code, String action, String args) {
        this.code = code;
        this.action = action;
        this.args = new JsonParser().parse(args);
    }

    public JsonElement getArgs() {
        return args;
    }

    public void putArgs(String args) {
        this.args = new JsonParser().parse(args);
    }
}
