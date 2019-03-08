package io.yosemiteblockchain.data.remote.chain.account;

import com.google.gson.annotations.Expose;

public class KeyWeight {

    @Expose
    private String key;

    @Expose
    private short weight;

    public KeyWeight() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }
}
