package io.yosemite.data.remote.api;

import com.google.gson.annotations.Expose;

public class AbiBinToJsonRequest {

    @Expose
    private String code;

    @Expose
    private String action;

    @Expose
    private String binargs;

    public AbiBinToJsonRequest(String code, String action, String binargs) {
        this.code = code;
        this.action = action;
        this.binargs = binargs;
    }
}
