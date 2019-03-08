package io.yosemiteblockchain.data.remote.chain.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Permission {

    @Expose
    @SerializedName("perm_name")
    private String permName;

    @Expose
    private String parent;

    @Expose
    @SerializedName("required_auth")
    private Authority requiredAuth;

    public Permission() {
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Authority getRequiredAuth() {
        return requiredAuth;
    }

    public void setRequiredAuth(Authority requiredAuth) {
        this.requiredAuth = requiredAuth;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }
}
