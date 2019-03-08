package io.yosemiteblockchain.data.remote.chain.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WaitWeight {

    @Expose
    @SerializedName("wait_sec")
    private Integer waitSec;

    @Expose
    private short weight;

    public Integer getWaitSec() {
        return waitSec;
    }

    public void setWaitSec(Integer waitSec) {
        this.waitSec = waitSec;
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }
}

