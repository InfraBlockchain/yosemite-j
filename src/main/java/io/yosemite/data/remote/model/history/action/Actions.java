package io.yosemite.data.remote.model.history.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Actions {

    @Expose
    private List<Action> actions;

    @Expose
    @SerializedName("last_irreversible_block")
    private Integer lastIrreversibleBlock;

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Integer getLastIrreversibleBlock() {
        return lastIrreversibleBlock;
    }

    public void setLastIrreversibleBlock(Integer lastIrreversibleBlock) {
        this.lastIrreversibleBlock = lastIrreversibleBlock;
    }
}
