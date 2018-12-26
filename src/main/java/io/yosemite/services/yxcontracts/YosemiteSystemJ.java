package io.yosemite.services.yxcontracts;

import io.yosemite.Consts;
import io.yosemite.crypto.ec.EosPublicKey;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.contract.ActionNewAccount;
import io.yosemite.data.remote.contract.ActionUpdateAuth;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.data.types.TypePublicKey;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Provides the methods for the yx.system Yosemite contract.
 */
public class YosemiteSystemJ extends YosemiteJ {

    public YosemiteSystemJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    /**
     * Creates the new account with its public key and the creator account.
     * The convenion of the account <code>name</code> follows
     * <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-standard-account-names">Naming Convention of YOSEMITE Standard Account Names</a>
     * Transaction fee is charged to the creator.
     * @param creator the name of the creator account
     * @param name the new account
     * @param ownerKey the public key
     * @param activeKey the public key
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createAccount(String creator, String name, String ownerKey,
                                                              String activeKey,
                                                              @Nullable TransactionParameters params) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(ownerKey)) throw new IllegalArgumentException("empty owner public key");
        if (StringUtils.isEmpty(activeKey)) throw new IllegalArgumentException("empty active public key");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name,
                TypePublicKey.from(new EosPublicKey(ownerKey)), TypePublicKey.from(new EosPublicKey(activeKey)));

        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ActionNewAccount.ACTION,
                gson.toJson(actionNewAccount),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Creates the new account with its public key and the creator account.
     * The convenion of the account <code>name</code> follows
     * <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-standard-account-names">Naming Convention of YOSEMITE Standard Account Names</a>
     * Transaction fee is charged to the creator.
     * @param creator the name of the creator account
     * @param name the new account
     * @param ownerAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param activeAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createAccount(String creator, String name,
                                                              TypeAuthority ownerAuthority,
                                                              TypeAuthority activeAuthority,
                                                              @Nullable TransactionParameters params) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (ownerAuthority == null) throw new IllegalArgumentException("empty owner public authority");
        if (activeAuthority == null) throw new IllegalArgumentException("empty active public authority");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name, ownerAuthority, activeAuthority);

        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ActionNewAccount.ACTION,
                gson.toJson(actionNewAccount),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Sets or updates the account's permission information for the given permission name. 
     * @param accountName account name
     * @param permissionName the permission name to set
     * @param parentPermissionName the parent permission name of target permission
     * @param authority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setAccountPermission(String accountName,
                                                                     String permissionName,
                                                                     String parentPermissionName,
                                                                     TypeAuthority authority,
                                                                     @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(accountName)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(permissionName)) throw new IllegalArgumentException("empty permission name");
        if (StringUtils.isEmpty(parentPermissionName)) throw new IllegalArgumentException("empty parent permission name");
        if (authority == null) throw new IllegalArgumentException("empty authority");

        ActionUpdateAuth updateAuth = new ActionUpdateAuth(accountName, permissionName, parentPermissionName, authority);
        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ActionUpdateAuth.ACTION,
                gson.toJson(updateAuth),
                buildCommonParametersWithDefaults(params, accountName));
    }

    /**
     * Sets or updates the account's permission for the active permission.
     * @param accountName account name
     * @param activeAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setAccountPermission(String accountName,
                                                                     TypeAuthority activeAuthority,
                                                                     @Nullable TransactionParameters params) {
        return setAccountPermission(accountName, Consts.ACTIVE_PERMISSION_NAME, Consts.OWNER_PERMISSION_NAME, activeAuthority, params);
    }
}
