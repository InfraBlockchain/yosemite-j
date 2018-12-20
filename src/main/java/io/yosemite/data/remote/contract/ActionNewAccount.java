package io.yosemite.data.remote.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.crypto.util.HexUtils;
import io.yosemite.data.types.*;

import static io.yosemite.Consts.YOSEMITE_SYSTEM_CONTRACT;

public class ActionNewAccount implements EosType.Packer {

    public static final String CONTRACT = YOSEMITE_SYSTEM_CONTRACT;
    public static final String ACTION = "newaccount";

    @Expose
    private TypeAccountName creator;

    @Expose
    private TypeAccountName name;

    @Expose
    private TypeAuthority owner;

    @Expose
    private TypeAuthority active;

    public ActionNewAccount(String creator, String name, TypeAuthority owner, TypeAuthority active) {
        this(new TypeAccountName(creator), new TypeAccountName(name), owner, active);
    }

    public ActionNewAccount(String creator, String name, TypePublicKey ownerPubKey, TypePublicKey activePubKey) {
        this(new TypeAccountName(creator), new TypeAccountName(name),
                new TypeAuthority(1, ownerPubKey, null), new TypeAuthority(1, activePubKey, null));
    }

    public ActionNewAccount(TypeAccountName creator, TypeAccountName name, TypeAuthority owner, TypeAuthority active) {
        this.creator = creator;
        this.name = name;
        this.owner = owner;
        this.active = active;
    }

    public String getCreatorName() {
        return this.creator.toString();
    }

    @Override
    public void pack(EosType.Writer writer) {
        this.creator.pack(writer);
        this.name.pack(writer);
        this.owner.pack(writer);
        this.active.pack(writer);
    }

    public String getAsHex() {
        EosType.Writer writer = new EosByteWriter(256);
        pack(writer);
        return HexUtils.toHex(writer.toBytes());
    }
}