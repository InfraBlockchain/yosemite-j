package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Optional;

/**
 * @author Eugene Chung
 */
public class TxIrreversibilityRequest extends EventBase {
    @Expose
    @SerializedName("tx_id")
    private String transactionId;

    @Expose
    @SerializedName("block_num_hint")
    private Optional<Long> blockNumberHint;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Optional<Long> getBlockNumberHint() {
        return blockNumberHint;
    }

    public void setBlockNumberHint(Long blockNumberHint) {
        this.blockNumberHint = Optional.ofNullable(blockNumberHint);
    }
}
