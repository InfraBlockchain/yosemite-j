package io.yosemite.data.remote.chain.account;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.TypePermissionLevel;

public class PermissionLevelWeight {

    @Expose
    private TypePermissionLevel permission;

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

    public TypePermissionLevel getPermission() {
        return permission;
    }

    public void setPermission(TypePermissionLevel permission) {
        this.permission = permission;
    }
}
