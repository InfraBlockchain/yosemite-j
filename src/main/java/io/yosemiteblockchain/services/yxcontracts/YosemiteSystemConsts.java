package io.yosemiteblockchain.services.yxcontracts;

import io.yosemiteblockchain.data.remote.contract.ActionLinkAuth;
import io.yosemiteblockchain.data.remote.contract.ActionNewAccount;
import io.yosemiteblockchain.data.remote.contract.ActionUnlinkAuth;
import io.yosemiteblockchain.data.remote.contract.ActionUpdateAuth;

public interface YosemiteSystemConsts {
    String ACTION_NEW_ACCOUNT = ActionNewAccount.ACTION;
    String ACTION_UPDATE_AUTH = ActionUpdateAuth.ACTION;
    String ACTION_LINK_AUTH = ActionLinkAuth.ACTION;
    String ACTION_UNLINK_AUTH = ActionUnlinkAuth.ACTION;
}
