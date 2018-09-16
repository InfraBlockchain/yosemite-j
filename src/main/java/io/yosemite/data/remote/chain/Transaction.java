package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.EosByteWriter;
import io.yosemite.data.types.EosType;
import io.yosemite.data.types.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transaction extends TransactionHeader {

    @Expose
    private List<Action> context_free_actions = Collections.emptyList();

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

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public int getContextFreeActionCount() {
        return (actions == null ? 0 : actions.size());
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

    public void setTransactionVoteTarget(String txVoteTarget) {
        TypeName voteTarget = new TypeName(txVoteTarget);
        EosByteWriter eosByteWriter = new EosByteWriter(8);
        voteTarget.pack(eosByteWriter);
        transaction_extensions.add(new TransactionExtension(TransactionExtensionField.TRANSACTION_VOTE_ACCOUNT, eosByteWriter.toBytes()));

    }
}

