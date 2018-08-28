package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class EventResponse {
    @Expose
    @SerializedName("req_id")
    private String requestId;

    @Expose
    private String name;

    public String getRequestId() {
        return requestId;
    }

    public String getName() {
        return name;
    }
}
