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
    @SerializedName("context_free_actions")
    private List<Action> contextFreeActions = new ArrayList<>();

    @Expose
    private List<Action> actions;

    @Expose
    @SerializedName("transaction_extensions")
    private List<TransactionExtension> transactionExtensions = new ArrayList<>();

    public Transaction() {
    }

    public Transaction(Transaction other) {
        super(other);
        this.contextFreeActions = deepCopyOnlyContainer(other.contextFreeActions);
        this.actions = deepCopyOnlyContainer(other.actions);
        this.transactionExtensions = other.transactionExtensions;
    }

    public void addAction(Action action) {
        if (null == actions) {
            actions = new ArrayList<>();
        }

        actions.add(action);
    }

    public void addActions(List<Action> actions) {
        if (null == this.actions) {
            this.actions = new ArrayList<>();
        }

        this.actions.addAll(actions);
    }

    private <T> List<T> deepCopyOnlyContainer(List<T> srcList) {
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

        writer.putCollection(contextFreeActions);
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

