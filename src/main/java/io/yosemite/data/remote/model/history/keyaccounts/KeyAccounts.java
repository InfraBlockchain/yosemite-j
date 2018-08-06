package io.yosemite.data.remote.model.history.keyaccounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KeyAccounts {

    @Expose
    @SerializedName("account_names")
    private List<String> accountNames;

    public List<String> getAccountNames() {
        return accountNames;
    }

    public void setAccountNames(List<String> accountNames) {
        this.accountNames = accountNames;
    }

}
