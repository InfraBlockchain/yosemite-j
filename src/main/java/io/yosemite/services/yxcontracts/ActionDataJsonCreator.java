package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import io.yosemite.data.remote.contract.ActionUpdateAuth;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

public class ActionDataJsonCreator {
    private static final Gson gson = Utils.createYosemiteJGson();

    public static String updateAuth(String accountName,
                                    String permissionName,
                                    String parentPermissionName,
                                    TypeAuthority authority) {
        if (StringUtils.isEmpty(accountName)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(permissionName)) throw new IllegalArgumentException("empty permission name");
        if (StringUtils.isEmpty(parentPermissionName)) throw new IllegalArgumentException("empty parent permission name");
        if (authority == null) throw new IllegalArgumentException("empty authority");

        ActionUpdateAuth updateAuth = new ActionUpdateAuth(accountName, permissionName, parentPermissionName, authority);
        return gson.toJson(updateAuth);
    }
}
