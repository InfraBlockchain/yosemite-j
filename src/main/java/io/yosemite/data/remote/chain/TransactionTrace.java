/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.yosemite.data.remote.chain;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionTrace {

    @Expose
    private String id;

    @Expose
    private TransactionReceiptHeader receipt;

    @Expose
    private long elapsed;

    @Expose
    @SerializedName("net_usage")
    private long netUsage; // uint64_t

    @Expose
    private boolean scheduled = false;

    @Expose
    @SerializedName("action_traces")
    private List<ActionTrace> actionTraces;

    @Expose
    private JsonElement except;

    public List<ActionTrace> getActionTraces() {
        return actionTraces;
    }

    @Override
    public String toString() {
        if (receipt == null) {
            return "empty receipt";
        }

        String result = ": " + receipt.getStatus();

        if (receipt.getNetUsageWords() < 0) {
            result += "<unknown>";
        } else {
            result += (receipt.getNetUsageWords() * 8);
        }
        result += " bytes ";


        if (receipt.getCpuUsageUs() < 0) {
            result += "<unknown>";
        } else {
            result += (receipt.getNetUsageWords() * 8);
        }
        result += " us\n";

        return result;
    }
}
