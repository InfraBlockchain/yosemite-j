package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Eugene Chung
 */
public class TxIrreversibilityResponse extends EventResponse {
    @Expose
    @SerializedName("response")
    private TxIrreversibilityResponseDetail response;

    public void setResponse(TxIrreversibilityResponseDetail response) {
        this.response = response;
    }
}
