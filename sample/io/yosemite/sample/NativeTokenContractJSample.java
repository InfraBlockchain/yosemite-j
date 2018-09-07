package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;

import java.util.EnumSet;
import java.util.Map;

public class NativeTokenContractJSample {
    // assume that the system depository is already registered
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";

    private static void processKYC(YosemiteSystemJ yxSystemJ, String accountName, EnumSet<KYCStatusType> flags) {
        String contract = "yx.identity";
        String action = "setidinfo";
        String data = "{\"identity_authority\":\"" + SYSTEM_DEPOSITORY_ACCOUNT + "\",\"account\":\"" + accountName + "\",\"type\":0,\"kyc\":" + KYCStatusType.getAsBitFlags(flags) + ",\"state\":0,\"data\":\"\"}";
        String[] permissions = new String[]{SYSTEM_DEPOSITORY_ACCOUNT + "@active"};

        PushedTransaction pushedTransaction = yxSystemJ.pushAction(contract, action, data, permissions).join();
        log("\nsetidinfo Transaction:\n" + pushedTransaction.getTransactionId());
    }

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");

        if (args.length > 0) {
            for (String arg : args) {
                if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        // create the user accounts
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, "ntuser1");
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        // KYC process done by Identity Authority Service for DKRW
        processKYC(yxSystemJ, "ntuser1", EnumSet.allOf(KYCStatusType.class));

        YosemiteNativeTokenJ yxNativeTokenJ = new YosemiteNativeTokenJ(apiClient);

        PushedTransaction pushedTransaction = yxNativeTokenJ.issueNativeToken(
                "ntuser1", "1000000.0000 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "my memo", null).join();
        log("Issue Native Token Transaction:" + pushedTransaction.getTransactionId());

        // transfer token with transacation fee payer as SYSTEM_DEPOSITORY_ACCOUNT
        pushedTransaction = yxNativeTokenJ.transferNativeTokenWithPayer(
                "ntuser1", SYSTEM_DEPOSITORY_ACCOUNT, "100000.0000 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "my memo", null).join();
        log("TransferWithPayer Native Token Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = yxNativeTokenJ.redeemNativeToken("100000.0000 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "my memo", null).join();
        log("Redeem Native Token Transaction:" + pushedTransaction.getTransactionId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //ignored
        }

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

    private static void waitForIrreversibility(YosemiteApiRestClient apiRestClient, PushedTransaction pushedTransaction) {
        int waitTime = apiRestClient.getTxExpirationInMillis() + 10000; // + 10 seconds
        do {
            Transaction tx = apiRestClient.getTransaction(pushedTransaction.getTransactionId()).execute();
            if (tx.getLastIrreversibleBlock() >= tx.getBlockNum()) {
                log(pushedTransaction.getTransactionId() + " is irreversible.");
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                waitTime = waitTime - 1000;
            }
        } while (waitTime > 0);

        if (waitTime <= 0) {
            throw new RuntimeException("Transaction could be expired");
        }
    }

    private static void createKeyPairAndAccount(YosemiteApiRestClient apiClient, YosemiteSystemJ yxSystemJ, String accountName) {
        String publicKey = apiClient.createKey().execute();
        PushedTransaction pushedTransaction = yxSystemJ.createAccount(
                "yosemite", accountName, publicKey, publicKey, null).join();
        log("Account Creation Transaction : " + pushedTransaction.getTransactionId());
    }

    private static void log(String s) {
        System.out.println(s);
    }
}
