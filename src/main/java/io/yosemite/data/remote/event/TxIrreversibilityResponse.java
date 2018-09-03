package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Eugene Chung
 */
public class TxIrreversibilityResponse extends EventBase {
    @Expose
    @SerializedName("tx_id")
    private String transactionId;

    @Expose
    private boolean dropped;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isDropped() {
        return dropped;
    }
}
