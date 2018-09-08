package io.yosemite.data.types;

/**
 * Represents the token symbol, the precision, name and the issuer.
 * For your information, please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.ntoken/README.md">yx.ntoken README</a>.
 */
public class TypeYxSymbol extends TypeSymbol implements EosType.Packer {
    public static char symbolIssuerDenominator = '@';

    private final TypeAccountName issuer;
    private volatile String form;

    public TypeYxSymbol(String from) {
        super(fromString(from));
        this.issuer = new TypeAccountName(parseIssuer(from));
    }

    public TypeYxSymbol(int precision, String symbolName, String issuer) {
        super(precision, symbolName);
        this.issuer = new TypeAccountName(issuer);
    }

    public TypeAccountName getIssuer() {
        return issuer;
    }

    public static TypeYxSymbol fromString(String from) {
        int index = from.indexOf(symbolIssuerDenominator);
        if (index < 0) {
            throw new IllegalArgumentException("missing at(@) in TypeYxSymbol");
        }
        String symbolPart = from.substring(0, index);
        String issuer = from.substring(index + 1);
        TypeSymbol typeSymbol = TypeSymbol.fromString(symbolPart);
        return new TypeYxSymbol(typeSymbol.decimals(), typeSymbol.name(), issuer);
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
