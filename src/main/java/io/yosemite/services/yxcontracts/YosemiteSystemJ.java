package io.yosemite.services.yxcontracts;

import io.yosemite.crypto.ec.EosPublicKey;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.contract.ActionNewAccount;
import io.yosemite.data.types.TypePublicKey;
import io.yosemite.services.CommonParameters;
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
     * @param params common parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createAccount(String creator, String name, String ownerKey,
                                                              String activeKey,
                                                              @Nullable CommonParameters params) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(ownerKey)) throw new IllegalArgumentException("empty owner public key");
        if (StringUtils.isEmpty(activeKey)) throw new IllegalArgumentException("empty active public key");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name,
                TypePublicKey.from(new EosPublicKey(ownerKey)), TypePublicKey.from(new EosPublicKey(activeKey)));

        return pushAction(ActionNewAccount.CONTRACT, ActionNewAccount.ACTION,
                gson.toJson(actionNewAccount),
                buildCommonParametersWithDefaults(params, creator));
    }
}
