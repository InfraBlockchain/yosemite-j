package io.yosemiteblockchain.data.remote.contract;

import com.google.gson.annotations.Expose;
import io.yosemiteblockchain.data.types.EosType;
import io.yosemiteblockchain.data.types.TypeAccountName;
import io.yosemiteblockchain.data.types.TypeAuthority;
import io.yosemiteblockchain.data.types.TypePublicKey;

public class ActionNewAccount implements EosType.Packer {

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

    @Override
    public void pack(EosType.Writer writer) {
        this.creator.pack(writer);
        this.name.pack(writer);
        this.owner.pack(writer);
        this.active.pack(writer);
    }
}