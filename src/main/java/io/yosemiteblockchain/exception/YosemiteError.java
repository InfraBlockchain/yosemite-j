package io.yosemiteblockchain.exception;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class YosemiteError {

    @Expose
    private Integer code;

    @Expose
    private String name;

    @Expose
    private String what;

    @Expose
    private YosemiteErrorDetails[] details;

    private YosemiteError(){

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public YosemiteErrorDetails[] getDetails() {
        return details;
    }

    public void setDetails(YosemiteErrorDetails[] details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("code", code)
                .append("name", name)
                .append("what", what)
                .toString();
    }
}
