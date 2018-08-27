package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * @author Eugene Chung
 */
public class TransactionReceipt extends TransactionReceiptHeader {
    @Expose
    private Map<String, PackedTransaction> trx;

    public TransactionReceipt() {
    }

    public Map<String, PackedTransaction> getTrx() {
        return trx;
    }

    public void setTrx(Map<String, PackedTransaction> trx) {
        this.trx = trx;
    }
}
