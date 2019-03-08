package io.yosemiteblockchain.data.remote.chain.account;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Authority {
    @Expose
    private int threshold;

    @Expose
    private List<KeyWeight> keys;

    @Expose
    private List<PermissionLevelWeight> accounts;

    @Expose
    private List<WaitWeight> waits;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public List<KeyWeight> getKeys() {
        return keys;
    }

    public void setKeys(List<KeyWeight> keys) {
        this.keys = keys;
    }

    public List<PermissionLevelWeight> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PermissionLevelWeight> accounts) {
        this.accounts = accounts;
    }

    public List<WaitWeight> getWaits() {
        return waits;
    }

    public void setWaits(List<WaitWeight> waits) {
        this.waits = waits;
    }
}
