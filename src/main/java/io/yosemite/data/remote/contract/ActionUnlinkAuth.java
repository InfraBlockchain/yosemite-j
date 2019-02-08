package io.yosemite.data.remote.contract;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.EosType;
import io.yosemite.data.types.TypeAccountName;
import io.yosemite.data.types.TypeActionName;

import static io.yosemite.Consts.YOSEMITE_SYSTEM_CONTRACT;

public class ActionUnlinkAuth implements EosType.Packer {

    public static final String CONTRACT = YOSEMITE_SYSTEM_CONTRACT;
    public static final String ACTION = "unlinkauth";

    @Expose
    private TypeAccountName account;

    @Expose
    private TypeAccountName code;

    @Expose
    private TypeActionName type;

    public ActionUnlinkAuth(String account, String code, String type) {
        this(new TypeAccountName(account), new TypeAccountName(code), new TypeActionName(type));
    }

    public ActionUnlinkAuth(TypeAccountName account, TypeAccountName code, TypeActionName type) {
        this.account = account;
        this.code = code;
        this.type = type;
    }

    @Override
    public void pack(EosType.Writer writer) {
        account.pack(writer);
        code.pack(writer);
        type.pack(writer);
    }

}