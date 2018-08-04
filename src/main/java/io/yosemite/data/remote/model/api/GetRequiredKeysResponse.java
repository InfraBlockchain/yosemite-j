package io.yosemite.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRequiredKeysResponse {

    @Expose
    @SerializedName("required_keys")
    private List<String> requiredKeys;

    public List<String> getRequiredKeys() {
        return requiredKeys;
    }
}
