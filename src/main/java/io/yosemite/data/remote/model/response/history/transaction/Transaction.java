package io.yosemite.data.remote.model.response.history.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.model.chain.ActionTrace;

import java.util.List;

public class Transaction {

    @Expose
    private String id;

    @Expose
    private Object trx;

    @Expose
    @SerializedName("block_time")
    private String blockTime;

    @Expose
    @SerializedName("block_num")
    private Integer blockNum;

    @Expose
    @SerializedName("last_irreversible_block")
    private Integer lastIrreversibleBlock;

    @Expose
    private List<ActionTrace> traces = null;

    @Expose
    private String status;

    @Expose
    @SerializedName("cpu_usage_us")
    private String cpuUsageUs;

    @Expose
    @SerializedName("net_usage_words")
    private String netUsageWords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getTrx() {
        return trx;
    }

    public void setTrx(Object trx) {
        this.trx = trx;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Integer blockNum) {
        this.blockNum = blockNum;
    }

    public Integer getLastIrreversibleBlock() {
        return lastIrreversibleBlock;
    }

    public void setLastIrreversibleBlock(Integer lastIrreversibleBlock) {
        this.lastIrreversibleBlock = lastIrreversibleBlock;
    }

    public List<ActionTrace> getTraces() {
        return traces;
    }

    public void setTraces(List<ActionTrace> traces) {
        this.traces = traces;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCpuUsageUs() {
        return cpuUsageUs;
    }

    public void setCpuUsageUs(String cpuUsageUs) {
        this.cpuUsageUs = cpuUsageUs;
    }

    public String getNetUsageWords() {
        return netUsageWords;
    }

    public void setNetUsageWords(String netUsageWords) {
        this.netUsageWords = netUsageWords;
    }
}
