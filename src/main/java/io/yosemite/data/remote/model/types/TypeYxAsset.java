package io.yosemite.data.remote.model.types;

public class TypeYxAsset extends TypeAsset implements EosType.Packer {
    private final TypeAccountName issuer;
    private volatile String form;

    public TypeYxAsset(String value, TypeAccountName issuer) {
        super(value);
        this.issuer = issuer;
    }

    public TypeYxAsset(long amount, TypeAccountName issuer) {
        super(amount);
        this.issuer = issuer;
    }

    public TypeYxAsset(long amount, TypeSymbol symbol, TypeAccountName issuer) {
        super(amount, symbol);
        this.issuer = issuer;
    }

    public TypeAccountName getIssuer() {
        return issuer;
    }

    @Override
    public String toString() {
        String form = this.form;
        if (form == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(super.toString()).append('@').append(issuer);
            form = builder.toString();
            this.form = form;
        }
        return form;
    }

    @Override
    public void pack(EosType.Writer writer) {
        super.pack(writer);
        issuer.pack(writer);
    }
}
