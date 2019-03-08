package io.yosemiteblockchain.data.remote.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemiteblockchain.data.remote.chain.SignedTransaction;

import java.util.ArrayList;
import java.util.List;

public class GetRequiredKeysRequest {

    @Expose
    private SignedTransaction transaction;

    @Expose
    @SerializedName("available_keys")
    private List<String> availableKeys;

    public GetRequiredKeysRequest(SignedTransaction transaction, List<String> keys) {
        this.transaction = transaction;

        if (null != keys) {
            availableKeys = new ArrayList<>(keys);
        } else {
            availableKeys = new ArrayList<>();
        }
    }
}
