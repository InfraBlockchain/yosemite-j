package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import io.yosemite.crypto.digest.Sha256;
import io.yosemite.data.types.EosByteWriter;
import io.yosemite.data.types.EosType;
import io.yosemite.data.types.TypeName;
import io.yosemite.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Transaction extends TransactionHeader {

    @Expose
    private List<Action> context_free_actions = new ArrayList<>();

    @Expose
    private List<Action> actions;

    @Expose
    private List<TransactionExtension> transaction_extensions = new ArrayList<>();

    public Transaction() {
    }

    public Transaction(Transaction other) {
        super(other);
        this.context_free_actions = deepCopyOnlyContainer(other.context_free_actions);
        this.actions = deepCopyOnlyContainer(other.actions);
        this.transaction_extensions = other.transaction_extensions;
    }

    public void addAction(Action msg) {
        if (null == actions) {
            actions = new ArrayList<>(1);
        }

        actions.add(msg);
    }

    <T> List<T> deepCopyOnlyContainer(List<T> srcList) {
        if (null == srcList) {
            return null;
        }

        List<T> newList = new ArrayList<>(srcList.size());
        newList.addAll(srcList);

        return newList;
    }

    @Override
    public void pack(EosType.Writer writer) {
        super.pack(writer);

        writer.putCollection(context_free_actions);
        writer.putCollection(actions);
        writer.putCollection(transaction_extensions);
    }

    public void setStringTransactionExtension(TransactionExtensionField field, String value) {

        if (StringUtils.isEmpty(value))
            return;

        TypeName typeName = new TypeName(value);
        EosByteWriter eosByteWriter = new EosByteWriter(8);
        typeName.pack(eosByteWriter);
        transaction_extensions.add(new TransactionExtension(field, eosByteWriter.toBytes()));
    }

    public String getId() {
        EosByteWriter eosByteWriter = new EosByteWriter(512);
        this.pack(eosByteWriter);
        return Sha256.from(eosByteWriter.toBytes()).toString();
    }
}

