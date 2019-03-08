package io.yosemiteblockchain.data.remote.chain.yosemite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemiteblockchain.data.types.TypeAccountName;

public class TransactionVote {
    @Expose
    private TypeAccountName to;

    @Expose
    @SerializedName("amt")
    private long amount;

    public TypeAccountName getTo() {
        return to;
    }

    public long getAmount() {
        return amount;
    }
}
