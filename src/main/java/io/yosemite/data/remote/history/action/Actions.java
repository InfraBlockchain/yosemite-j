package io.yosemite.data.remote.history.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Actions {

    @Expose
    private List<OrderedActionResult> actions;

    @Expose
    @SerializedName("last_irreversible_block")
    private Integer lastIrreversibleBlock;

    public List<OrderedActionResult> getActions() {
        return actions;
    }

    public void setActions(List<OrderedActionResult> actions) {
        this.actions = actions;
    }

    public Integer getLastIrreversibleBlock() {
        return lastIrreversibleBlock;
    }

    public void setLastIrreversibleBlock(Integer lastIrreversibleBlock) {
        this.lastIrreversibleBlock = lastIrreversibleBlock;
    }
}
