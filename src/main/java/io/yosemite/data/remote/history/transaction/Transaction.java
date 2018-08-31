package io.yosemite.data.remote.history.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.chain.ActionTrace;

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
    private List<ActionTrace> traces;


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
}
