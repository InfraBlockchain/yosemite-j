package io.yosemite.data.remote.chain.account;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.TypePermission;

public class PermissionLevelWeight {

    @Expose
    private TypePermission permission;

    @Expose
    private short weight;

    public PermissionLevelWeight() {
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }

    public TypePermission getPermission() {
        return permission;
    }

    public void setPermission(TypePermission permission) {
        this.permission = permission;
    }
}
