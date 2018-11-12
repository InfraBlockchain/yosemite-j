package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;

import java.util.EnumSet;
import java.util.Map;

public class NativeTokenContractJSample extends SampleCommon {
    // assume that the system depository is already registered
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        // Create Yosemite Client with servers of the same machine; transaction vote target for PoT is set to "d1"
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");
        apiClient.setTransactionVoteTarget(SYSTEM_DEPOSITORY_ACCOUNT);

        if (args.length > 0) {
            for (String arg : args) {
                if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        // [IMPORTANT NOTE ON PUBLIC KEY]
        // In most cases, the public keys of the accounts are already known to the service provider.
        // Before creating an account, you must create the key pair and must save the public key somewhere like RDB for the account creation.
        // For this sample, we get the public keys from the chain, but in real case, you should get them from the RDB.
        Account account = apiClient.getAccount(SYSTEM_DEPOSITORY_ACCOUNT).execute();
        String sysDepoPublicKey = account.getActivePublicKey();

        // create the user accounts
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        YosemiteNativeTokenJ yxNativeTokenJ = new YosemiteNativeTokenJ(apiClient);

        String user1PublicKey;
        try {
            user1PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, "yosemite", "ntuser1");

            PushedTransaction pushedTransaction = yxNativeTokenJ.issueNativeToken(
                    "ntuser1", "1000000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null, null).join();
            log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
            user1PublicKey = apiClient.getAccount("ntuser1").execute().getActivePublicKey();
        }

        // KYC process done by Identity Authority Service for DKRW
        processKYC(yxSystemJ, SYSTEM_DEPOSITORY_ACCOUNT, "ntuser1", EnumSet.allOf(KYCStatusType.class));

        PushedTransaction pushedTransaction = yxNativeTokenJ.issueNativeToken(
                "ntuser1", "1000000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "my memo", null, new String[]{sysDepoPublicKey}).join();
        log("Issue Native Token Transaction:" + pushedTransaction.getTransactionId());

        // transfer token with transacation fee payer as SYSTEM_DEPOSITORY_ACCOUNT
        pushedTransaction = yxNativeTokenJ.transferNativeToken(
                "ntuser1", SYSTEM_DEPOSITORY_ACCOUNT, "100000.00 DKRW", "my memo",
                null, new String[]{user1PublicKey, sysDepoPublicKey}).join();
        log("TransferWithPayer Native Token Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = yxNativeTokenJ.redeemNativeToken("100000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "my memo",
                null, new String[]{sysDepoPublicKey}).join();
        log("Redeem Native Token Transaction:" + pushedTransaction.getTransactionId());

        TableRow tableRow = yxNativeTokenJ.getNativeTokenAccountTotalBalance("ntuser1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        tableRow = yxNativeTokenJ.getNativeTokenStats(SYSTEM_DEPOSITORY_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }
    }

}
