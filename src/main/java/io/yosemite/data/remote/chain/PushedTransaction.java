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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.util.StringUtils;

/**
 * Represents the transaction instance which is accepted successfully by one of the Yosemite node.
 */
public class PushedTransaction {

    @Expose
    @SerializedName("transaction_id")
    private String transactionId;

    @Expose
    private TransactionTrace processed;

    /**
     * @return the string representation of SHA-256 hash of transaction bytes; you can use this value for retrieving
     *         transaction information from the Yosemite network.
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @return transaction processing result including pushed block number and block time
     */
    public TransactionTrace getTransactionTrace() {
        return processed;
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(transactionId) || (processed == null)) return "";

        return "Pushed transaction: " + transactionId + "\n" + processed.toString();
    }
}
