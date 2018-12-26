package io.yosemite.data.remote.contract;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.EosType;
import io.yosemite.data.types.TypeAccountName;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.data.types.TypePermissionName;

import static io.yosemite.Consts.YOSEMITE_SYSTEM_CONTRACT;

public class ActionUpdateAuth implements EosType.Packer {

    public static final String CONTRACT = YOSEMITE_SYSTEM_CONTRACT;
    public static final String ACTION = "updateauth";

    @Expose
    private TypeAccountName account;

    @Expose
    private TypePermissionName permission;

    @Expose
    private TypePermissionName parent;

    @Expose
    private TypeAuthority auth;

    public ActionUpdateAuth(String account, String permissionName, String parentPermissionName, TypeAuthority authority) {
        this(new TypeAccountName(account), new TypePermissionName(permissionName),new TypePermissionName(parentPermissionName), authority);
    }

    public ActionUpdateAuth(TypeAccountName account, TypePermissionName permission, TypePermissionName parent, TypeAuthority authority) {
        this.account = account;
        this.permission = permission;
        this.parent = parent;
        this.auth = authority;
    }

    @Override
    public void pack(EosType.Writer writer) {
        account.pack(writer);
        permission.pack(writer);
        parent.pack(writer);
        auth.pack(writer);
    }

}