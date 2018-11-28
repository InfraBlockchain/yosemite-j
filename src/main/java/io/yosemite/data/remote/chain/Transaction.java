package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
    @SerializedName("transaction_extensions")
    private List<TransactionExtension> transactionExtensions = new ArrayList<>();

    public Transaction() {
    }

    public Transaction(Transaction other) {
        super(other);
        this.context_free_actions = deepCopyOnlyContainer(other.context_free_actions);
        this.actions = deepCopyOnlyContainer(other.actions);
        this.transactionExtensions = other.transactionExtensions;
    }

    public void addAction(Action msg) {
        if (null == actions) {
            actions = new ArrayList<>();
        }

        actions.add(msg);
    }

    <T> List<T> deepCopyOnlyContainer(List<T> srcList) {
        if (null == srcList || srcList.isEmpty()) {
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
        writer.putCollection(transactionExtensions);
    }

    public void setStringTransactionExtension(TransactionExtensionField field, String value) {

        if (StringUtils.isEmpty(value)) return;

        TypeName typeName = new TypeName(value);
        EosByteWriter eosByteWriter = new EosByteWriter(8);
        typeName.pack(eosByteWriter);
        transactionExtensions.add(new TransactionExtension(field, eosByteWriter.toBytes()));
    }

    public String getId() {
        EosByteWriter eosByteWriter = new EosByteWriter(512);
        this.pack(eosByteWriter);
        return Sha256.from(eosByteWriter.toBytes()).toString();
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<TransactionExtension> getTransactionExtensions() {
        return transactionExtensions;
    }
}

