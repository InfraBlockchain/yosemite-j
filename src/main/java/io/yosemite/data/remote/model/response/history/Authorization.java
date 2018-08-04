
package io.yosemite.data.remote.model.response.history;

import com.google.gson.annotations.Expose;

public class Authorization {

    @Expose
    private String actor;

    @Expose
    private String permission;

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

}
