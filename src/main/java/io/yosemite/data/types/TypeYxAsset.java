package io.yosemite.data.types;

import static io.yosemite.data.types.TypeYxSymbol.symbolIssuerDenominator;

/**
 * Represents the token amount, its symbol and the issuer.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md">yx.ntoken README</a>.
 * @see TypeYxSymbol
 */
public class TypeYxAsset extends TypeAsset implements EosType.Packer {
    private final TypeAccountName issuer;
    private volatile String form;

    public TypeYxAsset(String from) {
        super(fromString(from));
        this.issuer = new TypeAccountName(parseIssuer(from));
    }

    public TypeYxAsset(String value, String issuer) {
        super(value);
        this.issuer = new TypeAccountName(issuer);
    }

    public TypeYxAsset(long amount, String issuer) {
        super(amount);
        this.issuer = new TypeAccountName(issuer);
    }

    public TypeYxAsset(long amount, TypeSymbol symbol, String issuer) {
        super(amount, symbol);
        this.issuer = new TypeAccountName(issuer);
    }

    public TypeAccountName getIssuer() {
        return issuer;
    }

    /**
     * Get TypeYxAsset instance from the Yosemite asset string.
     * @param from Yosemite asset string e.g. 1000.00 DKRW@sysdepo1
     * @return TypeYxAsset instance
     */
    public static TypeYxAsset fromString(String from) {
        int index = from.indexOf(symbolIssuerDenominator);
        if (index < 0) {
            throw new IllegalArgumentException("missing at(@) in TypeYxSymbol");
        }
        String assetPart = from.substring(0, index);
        String issuer = from.substring(index + 1);
        return new TypeYxAsset(assetPart, issuer);
    }

    public static String parseIssuer(String from) {
        int index = from.indexOf(symbolIssuerDenominator);
        if (index < 0) {
            throw new IllegalArgumentException("missing at(@) in TypeYxSymbol");
        }
        return from.substring(index + 1);
    }

    @Override
    public String toString() {
        String form = this.form;
        if (form == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(super.toString()).append(symbolIssuerDenominator).append(issuer);
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
