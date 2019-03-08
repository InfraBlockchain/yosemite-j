package io.yosemiteblockchain.data.remote.chain;

import io.yosemiteblockchain.data.types.EosType;

public class TransactionExtension implements EosType.Packer {
    private final short field;
    private final byte[] data;

    public TransactionExtension(TransactionExtensionField field, byte[] data) {
        this.field = field.getValue();
        this.data = data;
    }

    public TransactionExtension(short field, byte[] data) {
        this.field = field;
        this.data = data;
    }

    public short getField() {
        return field;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putShortLE(field);
        writer.putVariableUInt(data.length);
        writer.putBytes(data);
    }
}
