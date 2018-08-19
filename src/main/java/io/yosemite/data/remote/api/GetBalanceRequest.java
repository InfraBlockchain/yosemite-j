package io.yosemite.data.remote.api;

import com.google.gson.annotations.Expose;
import io.yosemite.data.types.TypeAccountName;

public class GetBalanceRequest extends GetRequestForCurrency {

    @Expose
    private TypeAccountName account;

    public GetBalanceRequest(String tokenContract, String account, String symbol) {
        super(tokenContract, symbol);
        this.account = new TypeAccountName(account);
    }
}
