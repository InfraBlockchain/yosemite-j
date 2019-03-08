package io.yosemiteblockchain.sample;

import io.yosemiteblockchain.Consts;
import io.yosemiteblockchain.data.remote.chain.PushedTransaction;
import io.yosemiteblockchain.data.remote.chain.TokenInfo;
import io.yosemiteblockchain.data.remote.chain.account.Account;
import io.yosemiteblockchain.data.types.TypeAsset;
import io.yosemiteblockchain.services.TransactionParameters;
import io.yosemiteblockchain.services.YosemiteApiClientFactory;
import io.yosemiteblockchain.services.YosemiteApiRestClient;
import io.yosemiteblockchain.services.yxcontracts.KYCStatusType;
import io.yosemiteblockchain.services.yxcontracts.StandardToken;
import io.yosemiteblockchain.services.yxcontracts.YosemiteSystemJ;

import java.util.EnumSet;

/**
 * Standard token sample for the token registered as fee token(or system token).
 */
public class StandardTokenSample1 extends SampleCommon {
    private static final String SYSTEM_TOKEN_ACCOUNT = "systoken.a";
    private static final String IDENTITY_AUTHORITY_ACCOUNT = "idauth.a";
    private static final String TEST_ACCOUNT = "tkuserxxxxxx";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
            Consts.TESNET_SENTINEL_NODE_ADDRESS, Consts.DEFAULT_KEYOS_HTTP_URL);
        apiClient.setTransactionVoteTarget("producer.a");

        if (args.length > 0) {
            for (String arg : args) {
                if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        // For this sample, we get the public key from the chain.
        Account account = apiClient.getAccount(SYSTEM_TOKEN_ACCOUNT).execute();
        String sysTokenPublicKey = account.getActivePublicKey();
        log("System token public key = " + sysTokenPublicKey);

        // create the user accounts
        String tokenUserPublicKey;
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        try {
            tokenUserPublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, IDENTITY_AUTHORITY_ACCOUNT, TEST_ACCOUNT);
            processKYC(yxSystemJ, IDENTITY_AUTHORITY_ACCOUNT, TEST_ACCOUNT, EnumSet.allOf(KYCStatusType.class));
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
            tokenUserPublicKey = apiClient.getAccount(TEST_ACCOUNT).execute().getActivePublicKey();
        }
        log("Test user public key = " + tokenUserPublicKey);
        // Test user private key = YPV_5HwSdWSAxu5QSVPN6dB9gtTKXFLKSnQTYGa7eLRN91aiw98kCwL

        StandardToken standardToken = new StandardToken(apiClient);
        TransactionParameters txParametersForSystemToken = TransactionParameters.Builder().
            addPublicKey(sysTokenPublicKey).build();
        PushedTransaction pushedTransaction = standardToken.issueToken(
            TEST_ACCOUNT, "10000.0000 DUSD", SYSTEM_TOKEN_ACCOUNT, "", txParametersForSystemToken).join();
        log("Issue Transaction : " + pushedTransaction.getTransactionId() + ", block number=" + pushedTransaction.getTransactionTrace().getBlockNumer());

        TransactionParameters txParametersForUser1 = TransactionParameters.Builder().
            addPublicKey(tokenUserPublicKey).
            addPublicKey(sysTokenPublicKey).
            setTransactionFeePayer(SYSTEM_TOKEN_ACCOUNT).build();

        log("");
        log("[Token Info]");
        TokenInfo tokenInfo = standardToken.getTokenInfo(SYSTEM_TOKEN_ACCOUNT).join();
        log(tokenInfo.toString());

        log("");
        log("[Account's Token Balance]");
        TypeAsset accountBalance = standardToken.getAccountBalance(SYSTEM_TOKEN_ACCOUNT, TEST_ACCOUNT).join();
        String amount = accountBalance.toString();
        log(amount);

        pushedTransaction = standardToken.transferToken(TEST_ACCOUNT, SYSTEM_TOKEN_ACCOUNT, amount,
            SYSTEM_TOKEN_ACCOUNT, "my memo", txParametersForUser1).join();
        log("Transfer Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = standardToken.redeemToken(amount, SYSTEM_TOKEN_ACCOUNT, "my memo",
            txParametersForSystemToken).join();
        log("Redeem Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        log("");
        log("[Token Info]");
        tokenInfo = standardToken.getTokenInfo(SYSTEM_TOKEN_ACCOUNT).join();
        log(tokenInfo.toString());

        log("");
        log("[Account's Token Balance]");
        accountBalance = standardToken.getAccountBalance(SYSTEM_TOKEN_ACCOUNT, TEST_ACCOUNT).join();
        log(accountBalance.toString());
    }
}
