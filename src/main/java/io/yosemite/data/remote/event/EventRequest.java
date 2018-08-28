package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class EventRequest {
    @Expose
    @SerializedName("req_id")
    private String requestId;

    @Expose
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
