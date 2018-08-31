package io.yosemite.exception;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public class YosemiteApiError {

    @Expose
    private int code;

    @Expose
    private String message;

    @Expose
    private YosemiteError error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public YosemiteError getError() {
        return error;
    }

    public void setError(YosemiteError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("message", message)
                .append("code", code)
                .toString();
    }

    public String getDetailedMessage() {
        return error == null ? message : message + ": " + error.getWhat();
    }

    public Integer getEosErrorCode() {
        return error == null ? null : error.getCode();
    }
}
