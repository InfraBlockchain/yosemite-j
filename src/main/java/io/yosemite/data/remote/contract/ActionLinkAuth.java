package io.yosemite.data.remote.contract;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.*;

import static io.yosemite.Consts.YOSEMITE_SYSTEM_CONTRACT;

public class ActionLinkAuth implements EosType.Packer {

    public static final String CONTRACT = YOSEMITE_SYSTEM_CONTRACT;
    public static final String ACTION = "linkauth";

    @Expose
    private TypeAccountName account;

    @Expose
    private TypeAccountName code;

    @Expose
    private TypeActionName type;

    @Expose
    private TypePermissionName requirement;

    public ActionLinkAuth(String account, String code, String type, String requirement) {
        this(new TypeAccountName(account), new TypeAccountName(code), new TypeActionName(type), new TypePermissionName(requirement));
    }

    public ActionLinkAuth(TypeAccountName account, TypeAccountName code, TypeActionName type, TypePermissionName requirement) {
        this.account = account;
        this.code = code;
        this.type = type;
        this.requirement = requirement;
    }

    @Override
    public void pack(EosType.Writer writer) {
        account.pack(writer);
        code.pack(writer);
        type.pack(writer);
        requirement.pack(writer);
    }

}