package io.yosemite.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TransactionReceiptHeader {

//    enum status_enum {
//        executed  = 0, ///< succeed, no error handler executed
//        soft_fail = 1, ///< objectively failed (not executed), error handler executed
//        hard_fail = 2, ///< objectively failed and error handler objectively failed thus no state change
//        delayed   = 3  ///< transaction delayed
//    };

    @Expose
    private String status;

    @Expose
    @SerializedName("cpu_usage_us")
    private long cpuUsageUs;   ///< total billed CPU usage (microseconds)

    @Expose
    @SerializedName("net_usage_words")
    private long netUsageWords;///<  total billed NET usage, so we can reconstruct resource state when skipping context free data... hard failures...

    public String getStatus() {
        return status;
    }

    public long getCpuUsageUs() {
        return cpuUsageUs;
    }

    public long getNetUsageWords() {
        return netUsageWords;
    }
}
