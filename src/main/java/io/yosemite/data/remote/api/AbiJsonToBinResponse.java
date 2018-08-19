package io.yosemite.data.remote.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AbiJsonToBinResponse {

    @Expose
    private String binargs;

    @Expose
    @SerializedName("required_scopes")
    private List<String> requiredScopes;

    @Expose
    @SerializedName("required_auth")
    private List<String> requiredAuth;

    public AbiJsonToBinResponse(String binargs, List<String> requiredScopes, List<String> requiredAuth) {
        this.binargs = binargs;
        this.requiredScopes = requiredScopes;
        this.requiredAuth = requiredAuth;
    }

    public String getBinargs() {
        return binargs;
    }

    public List<String> getRequiredScopes() {
        return requiredScopes;
    }

    public List<String> getRequiredAuth() {
        return requiredAuth;
    }
}
