
package io.yosemite.data.remote.model.response.history.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.model.chain.ActionTrace;

public class Action {

    @Expose
    @SerializedName("account_action_seq")
    private Integer accountActionSeq;

    @Expose
    @SerializedName("action_trace")
    private ActionTrace actionTrace;

    @Expose
    @SerializedName("block_num")
    private Integer blockNum;

    @Expose
    @SerializedName("block_time")
    private String blockTime;

    @Expose
    @SerializedName("global_action_seq")
    private Integer globalActionSeq;

    public Integer getAccountActionSeq() {
        return accountActionSeq;
    }

    public void setAccountActionSeq(Integer accountActionSeq) {
        this.accountActionSeq = accountActionSeq;
    }

    public ActionTrace getActionTrace() {
        return actionTrace;
    }

    public void setActionTrace(ActionTrace actionTrace) {
        this.actionTrace = actionTrace;
    }

    public Integer getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Integer blockNum) {
        this.blockNum = blockNum;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getGlobalActionSeq() {
        return globalActionSeq;
    }

    public void setGlobalActionSeq(Integer globalActionSeq) {
        this.globalActionSeq = globalActionSeq;
    }

}
