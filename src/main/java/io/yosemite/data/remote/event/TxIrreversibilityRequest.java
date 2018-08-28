package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Eugene Chung
 */
public class TxIrreversibilityRequest extends EventRequest {
    @Expose
    @SerializedName("params")
    private TxIrreversibilityParameters parameters;

    public void setParameters(TxIrreversibilityParameters parameters) {
        this.parameters = parameters;
    }
}
