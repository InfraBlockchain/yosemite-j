package io.yosemite.data.remote.history.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.data.remote.chain.ActionTrace;
import io.yosemite.util.Utils;

import java.text.ParseException;
import java.util.List;

public class Transaction {

    @Expose
    private String id;

    @Expose
    @SerializedName("bTime")
    private Timestamp blockTime;

    @Expose
    @SerializedName("bNum")
    private Integer blockNum;

    @Expose
    @SerializedName("irrAt")
    private Timestamp irreversibleAt;

    @Expose
    @SerializedName("expiration")
    private String expirationAt;

    @Expose
    @SerializedName("action_traces")
    private List<ActionTrace> traces;

    public String getId() {
        return id;
    }

    public Timestamp getBlockTime() {
        return blockTime;
    }

    public Integer getBlockNum() {
        return blockNum;
    }

    public Timestamp getIrreversibleAt() {
        return irreversibleAt;
    }

    public List<ActionTrace> getTraces() {
        return traces;
    }

    public Timestamp getExpirationAt() {
        Timestamp timestamp = new Timestamp();
        try {
            timestamp.setTimestamp(Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get().parse(this.expirationAt).getTime());
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("invalid time format '"+this.expirationAt+"'", e);
        }
    }
}
