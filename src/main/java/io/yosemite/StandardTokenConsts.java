package io.yosemite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface StandardTokenConsts {
    String ACTION_SET_TOKEN_META = "settokenmeta";
    String ACTION_ISSUE = "issue";
    String ACTION_REDEEM = "redeem";
    String ACTION_TRANSFER = "transfer";

    Set<String> STANDARD_TOKEN_ACTIONS = new HashSet<>(Arrays.asList(
        ACTION_SET_TOKEN_META,
        ACTION_ISSUE,
        ACTION_REDEEM,
        ACTION_TRANSFER
    ));
}