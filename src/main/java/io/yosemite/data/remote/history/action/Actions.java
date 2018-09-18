package io.yosemite.data.remote.history.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Actions {

    @Expose
    @SerializedName("lastIrrBlkNum")
    private Integer lastIrreversibleBlock;

    @Expose
    private List<OrderedActionResult> actions;

    public Integer getLastIrreversibleBlock() {
        return lastIrreversibleBlock;
    }

    public List<OrderedActionResult> getActions() {
        return actions;
    }
}
