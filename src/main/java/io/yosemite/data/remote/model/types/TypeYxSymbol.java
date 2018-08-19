package io.yosemite.data.remote.model.types;

public class TypeYxSymbol extends TypeSymbol implements EosType.Packer {
    private final TypeAccountName issuer;
    private volatile String form;

    public TypeYxSymbol(TypeAccountName issuer) {
        this.issuer = issuer;
    }

    public TypeYxSymbol(int precision, CharSequence symbolName, TypeAccountName issuer) {
        super(precision, symbolName);
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
