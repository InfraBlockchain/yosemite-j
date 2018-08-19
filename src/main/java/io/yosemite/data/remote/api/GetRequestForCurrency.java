package io.yosemite.data.remote.api;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.TypeName;
import io.yosemite.util.StringUtils;

public class GetRequestForCurrency {
    @Expose
    protected boolean json = false;

    @Expose
    protected TypeName code;

    @Expose
    protected String symbol;

    public GetRequestForCurrency(String tokenContract, String symbol) {
        this.code = new TypeName(tokenContract);
        this.symbol = StringUtils.isEmpty(symbol) ? null : symbol;
    }
}
