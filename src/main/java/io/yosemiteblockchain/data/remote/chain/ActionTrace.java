package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActionTrace {

    @Expose
    private ActionReceipt receipt;

    @Expose
    private Action act;

    @Expose
    @SerializedName("context_free")
    private boolean contextFree;

    @Expose
    private long elapsed;

    @Expose
    private String console;

    @Expose
    @SerializedName("trx_id")
    private String trxId;

    @Expose
    @SerializedName("block_num")
    private long blockNumer;

    @Expose
    @SerializedName("block_time")
    private String blockTime;

    @Expose
    private JsonElement except;

    @Expose
    @SerializedName("inline_traces")
    private List<ActionTrace> inlineTraces;

    public ActionReceipt getReceipt() {
        return receipt;
    }

    public Action getAct() {
        return act;
    }

    public long getElapsed() {
        return elapsed;
    }

    public String getConsole() {
        return console;
    }

    public String getTrxId() {
        return trxId;
    }

    public boolean isContextFree() {
        return contextFree;
    }

    public long getBlockNumer() {
        return blockNumer;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public JsonElement getExcept() {
        return except;
    }

    public List<ActionTrace> getInlineTraces() {
        return inlineTraces;
    }
}
