package org.yosemitex.data.remote.model.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActionTrace {

    @Expose
    private ActionReceipt receipt;

    @Expose
    private Action act;

    @Expose
    private long elapsed;

    @Expose
    @SerializedName("cpu_usage")
    private long cpuUsage;

    @Expose
    private String console;

    @Expose
    @SerializedName("total_cpu_usage")
    private long totalCpuUsage;

    @Expose
    @SerializedName("trx_id")
    private String trxId;

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

    public long getCpuUsage() {
        return cpuUsage;
    }

    public String getConsole() {
        return console;
    }

    public long getTotalCpuUsage() {
        return totalCpuUsage;
    }

    public String getTrxId() {
        return trxId;
    }

    public List<ActionTrace> getInlineTraces() {
        return inlineTraces;
    }
}
