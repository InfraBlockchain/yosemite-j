package io.yosemite.data.remote.history.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.chain.Action;
import io.yosemite.data.remote.chain.ActionReceipt;
import io.yosemite.data.remote.history.transaction.Timestamp;

public class OrderedActionResult {

    @Expose
    @SerializedName("receipt")
    private ActionReceipt receipt;

    @Expose
    @SerializedName("act")
    private Action action;

    @Expose
    @SerializedName("block_num")
    private Integer blockNum;

    @Expose
    @SerializedName("block_time")
    private String blockTime;

    @Expose
    @SerializedName("trx_id")
    private String transactionId;

    public ActionReceipt getReceipt() {
        return receipt;
    }

    public Action getAction() {
        return action;
    }

    public Integer getBlockNum() {
        return blockNum;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
