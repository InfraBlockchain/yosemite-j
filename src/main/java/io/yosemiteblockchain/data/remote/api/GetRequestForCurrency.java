package io.yosemiteblockchain.data.remote.api;

import com.google.gson.annotations.Expose;
import io.yosemiteblockchain.data.types.TypeName;
import io.yosemiteblockchain.util.StringUtils;

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
