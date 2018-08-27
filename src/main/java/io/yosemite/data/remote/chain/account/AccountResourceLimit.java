package io.yosemite.data.remote.chain.account;

import com.google.gson.annotations.Expose;

/**
 * @author Eugene Chung
 */
public class AccountResourceLimit {
    @Expose
    private long used;

    @Expose
    private long available;

    @Expose
    private long max;

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
}
