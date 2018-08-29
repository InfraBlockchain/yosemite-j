package io.yosemite.data.remote.event;

import com.google.gson.annotations.Expose;

/**
 * @author Eugene Chung
 */
public class ErrorResponse extends EventBase {
    @Expose
    private int code;

    @Expose
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
