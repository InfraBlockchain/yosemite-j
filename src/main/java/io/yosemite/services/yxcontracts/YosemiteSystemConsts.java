package io.yosemite.services.yxcontracts;

import io.yosemite.data.remote.contract.ActionLinkAuth;
import io.yosemite.data.remote.contract.ActionNewAccount;
import io.yosemite.data.remote.contract.ActionUnlinkAuth;
import io.yosemite.data.remote.contract.ActionUpdateAuth;

public interface YosemiteSystemConsts {
    String ACTION_NEW_ACCOUNT = ActionNewAccount.ACTION;
    String ACTION_UPDATE_AUTH = ActionUpdateAuth.ACTION;
    String ACTION_LINK_AUTH = ActionLinkAuth.ACTION;
    String ACTION_UNLINK_AUTH = ActionUnlinkAuth.ACTION;
}
