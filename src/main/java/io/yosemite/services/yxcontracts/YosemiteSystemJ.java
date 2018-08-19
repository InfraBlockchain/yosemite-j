package io.yosemite.services.yxcontracts;

import io.yosemite.crypto.ec.EosPublicKey;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.contract.ActionNewAccount;
import io.yosemite.data.types.TypePublicKey;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class YosemiteSystemJ extends YosemiteJ {

    public YosemiteSystemJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    public CompletableFuture<PushedTransaction> createAccount(String creator, String name, String ownerKey,
                                                              String activeKey, @Nullable final String[] permissions) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(ownerKey)) throw new IllegalArgumentException("empty owner public key");
        if (StringUtils.isEmpty(activeKey)) throw new IllegalArgumentException("empty active public key");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name,
                TypePublicKey.from(new EosPublicKey(ownerKey)), TypePublicKey.from(new EosPublicKey(activeKey)));

        return pushAction(ActionNewAccount.CONTRACT, ActionNewAccount.ACTION,
                gson.toJson(actionNewAccount), isEmptyArray(permissions) ?
                        new String[]{creator + "@active"} : permissions);
    }
}
