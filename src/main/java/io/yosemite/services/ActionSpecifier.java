package io.yosemite.services;

import io.yosemite.Consts;
import io.yosemite.data.types.TypePermission;
import io.yosemite.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the specifier with its contract name(value0), action name(value1), and the json-formatted action data(value2).
 * Optionally, you can add the list of account permissions(authorizations) like (accountName, active).
 * If any permission is not added here, the permission list of TransactionParameters will be used.
 * @see TransactionParameters#getPermissions()
 * @see io.yosemite.services.TransactionParameters.TransactionParametersBuilder#addPermission(String)
 * @see io.yosemite.services.TransactionParameters.TransactionParametersBuilder#addPermission(String, String)
 */
public final class ActionSpecifier extends Triplet<String, String, String> {
    private final List<TypePermission> permissions = new ArrayList<>();

    public ActionSpecifier(String contract, String name, String data) {
        super(contract, name, data);
    }

    public void addPermission(String accountName) {
        addPermission(accountName, Consts.ACTIVE_PERMISSION_NAME);
    }

    public void addPermission(String accountName, String permissionName) {
        if (accountName == null) throw new IllegalArgumentException("accountName cannot be null.");
        if (permissionName == null) throw new IllegalArgumentException("permissionName cannot be null.");
        TypePermission typePermission = new TypePermission(accountName, permissionName);
        if (!permissions.contains(typePermission)) {
            permissions.add(typePermission);
        }
    }

    public Collection<TypePermission> getPermissions() {
        return permissions;
    }
}
