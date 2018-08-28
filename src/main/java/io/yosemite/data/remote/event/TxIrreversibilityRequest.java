package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Eugene Chung
 */
public class TxIrreversibilityRequest {
    //{"name":"tx_irreversibility","parameters":{"tx_id":"a4f2bfe30205cf8805aa17014759152414bc6db6879b9de465fefe91cd118db5"}}
    @Expose
    private final String name = "tx_irreversibility";

    @Expose
    @SerializedName("params")
    private TxIrreversibilityParameters parameters;

    public void setParameters(TxIrreversibilityParameters parameters) {
        this.parameters = parameters;
    }
}
