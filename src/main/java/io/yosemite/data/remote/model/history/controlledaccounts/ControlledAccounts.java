package io.yosemite.data.remote.model.history.controlledaccounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ControlledAccounts {

    @Expose
    @SerializedName("controlled_accounts")
    private List<String> controlledAccounts;

    public List<String> getControlledAccounts() {
        return controlledAccounts;
    }

    public void setControlledAccounts(List<String> controlledAccounts) {
        this.controlledAccounts = controlledAccounts;
    }

}
