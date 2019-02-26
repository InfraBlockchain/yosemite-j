package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.yosemite.Consts;
import io.yosemite.data.remote.contract.ActionUpdateAuth;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

/**
 * Represents the helper class for creating json-formatted string of various action data for Yosemite contracts.
 */
public class ActionDataJsonCreator {
    private static final Gson gson = Utils.createYosemiteJGson();

    public static String updateAuth(String accountName,
                                    String permissionName,
                                    String parentPermissionName,
                                    TypeAuthority authority) {
        if (StringUtils.isEmpty(accountName)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(permissionName)) throw new IllegalArgumentException("empty permission name");
        if (StringUtils.isEmpty(parentPermissionName) && !Consts.OWNER_PERMISSION_NAME.equals(permissionName)) {
            throw new IllegalArgumentException("empty parent permission name");
        }
        if (authority == null) throw new IllegalArgumentException("empty authority");

        ActionUpdateAuth updateAuth = new ActionUpdateAuth(accountName, permissionName, parentPermissionName, authority);
        return gson.toJson(updateAuth);
    }

    public static String transferToken(String from, String to, String amount, String issuer, String tag) {
        if (StringUtils.isEmpty(from)) throw new IllegalArgumentException("wrong from");
        if (StringUtils.isEmpty(to)) throw new IllegalArgumentException("wrong to");
        if (StringUtils.isEmpty(amount)) throw new IllegalArgumentException("wrong amount");
        if (StringUtils.isEmpty(issuer)) throw new IllegalArgumentException("wrong issuer");
        if (tag != null && tag.length() > 256) throw new IllegalArgumentException("too long tag");

        JsonObject object = new JsonObject();
        object.addProperty("t", issuer);
        object.addProperty("from", from);
        object.addProperty("to", to);
        object.addProperty("qty", amount);
        object.addProperty("tag", tag);

        return gson.toJson(object);
    }

}
