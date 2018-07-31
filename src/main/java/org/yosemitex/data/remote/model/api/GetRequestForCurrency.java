package org.yosemitex.data.remote.model.api;

import com.google.gson.annotations.Expose;
import org.yosemitex.data.remote.model.types.TypeName;
import org.yosemitex.util.StringUtils;

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
