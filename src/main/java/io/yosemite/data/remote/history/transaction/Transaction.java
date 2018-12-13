package io.yosemite.data.remote.history.transaction;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.chain.Action;
import io.yosemite.data.remote.chain.ActionTrace;
import io.yosemite.data.remote.chain.TransactionExtension;
import io.yosemite.data.remote.chain.TransactionReceipt;
import io.yosemite.data.remote.chain.yosemite.TransactionVote;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    @Expose
    private String id;

    @Expose
    @SerializedName("block_num")
    private int blockNum;

    @Expose
    @SerializedName("block_time")
    private String blockTime;

    @Expose
    private long elapsed;

    @Expose
    private TransactionReceipt receipt;

    @Expose
    @SerializedName("fee_payer")
    private String feePayer;

    @Expose
    @SerializedName("net_usage")
    private long netUsage;

    @Expose
    @SerializedName("action_traces")
    private List<ActionTrace> actionTraces;

    @Expose
    private JsonElement except;

    @Expose
    private boolean scheduled;

    @Expose
    @SerializedName("trx_vote")
    private TransactionVote transactionVote;

    @Expose
    private boolean accepted;

    @Expose
    private List<Action> actions;

    @Expose
    @SerializedName("context_free_actions")
    private List<Action> contextFreeActions = new ArrayList<>();

    @Expose
    @SerializedName("context_free_data")
    private List<String> contextFreeData = new ArrayList<>();

    @Expose
    @SerializedName("delay_sec")
    private long delaySec;

    @Expose
    @SerializedName("expiration")
    private String expiration;

    @Expose
    private boolean implicit;

    @Expose
    @SerializedName("max_net_usage_words")
    private long maxNetUsageWords;

    @Expose
    @SerializedName("max_cpu_usage_ms")
    private long maxCpuUsageMs;

    @Expose
    @SerializedName("ref_block_num")
    private int refBlockNum;

    @Expose
    @SerializedName("ref_block_prefix")
    private long refBlockPrefix;

    @Expose
    @SerializedName("transaction_extensions")
    private List<TransactionExtension> transactionExtensions = new ArrayList<>();

    @Expose
    private List<String> signatures = new ArrayList<>();

    @Expose
    private JsonElement signing_keys;

    @Expose
    @SerializedName("block_id")
    private String blockId;

    @Expose
    @SerializedName("irrAt")
    private Timestamp irreversibleAt;

    public String getId() {
        return id;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public Timestamp getIrreversibleAt() {
        return irreversibleAt;
    }
    
    public List<ActionTrace> getActionTraces() {
        return actionTraces;
    }

    public String getExpiration() {
        return expiration;
    }

    public long getElapsed() {
        return elapsed;
    }

    public String getFeePayer() {
        return feePayer;
    }

    public long getNetUsage() {
        return netUsage;
    }

    public TransactionReceipt getReceipt() {
        return receipt;
    }

    public JsonElement getExcept() {
        return except;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public TransactionVote getTransactionVote() {
        return transactionVote;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<Action> getContextFreeActions() {
        return contextFreeActions;
    }

    public List<String> getContextFreeData() {
        return contextFreeData;
    }

    public long getDelaySec() {
        return delaySec;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public long getMaxNetUsageWords() {
        return maxNetUsageWords;
    }

    public long getMaxCpuUsageMs() {
        return maxCpuUsageMs;
    }

    public int getRefBlockNum() {
        return refBlockNum;
    }

    public long getRefBlockPrefix() {
        return refBlockPrefix;
    }

    public List<TransactionExtension> getTransactionExtensions() {
        return transactionExtensions;
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public JsonElement getSigning_keys() {
        return signing_keys;
    }
}
