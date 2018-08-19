package io.yosemite.data.types;

import java.util.Objects;

public class TypeDcId implements EosType.Packer {
    private final TypeAccountName creator;
    private final long sequence;

    public TypeDcId(TypeAccountName creator, long sequence) {
        this.creator = creator;
        this.sequence = sequence;
    }

    public TypeAccountName getCreator() {
        return creator;
    }

    public long getSequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDcId dcId = (TypeDcId) o;
        return sequence == dcId.sequence &&
                Objects.equals(creator, dcId.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creator, sequence);
    }

    @Override
    public String toString() {
        return "TypeDcId{" +
                "creator=" + creator +
                ", sequence=" + sequence +
                '}';
    }

    @Override
    public void pack(EosType.Writer writer) {
        creator.pack(writer);
        writer.putLongLE(sequence);
    }
}
