package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;
import io.yosemite.services.yxcontracts.YosemiteTokenJ;

import java.util.EnumSet;
import java.util.Map;

public class TokenContractJSample {
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";
    private static final String TOKEN_PROVIDER_ACCOUNT = "tkprovider";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");

        if (args.length > 0) {
            for (String arg : args) {
                if ("-prepare".equals(arg)) {
                    prepareTokenProvider(apiClient);
                    return;
                } else if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        // create the user accounts
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, "user1");
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        YosemiteTokenJ yxTokenJ = new YosemiteTokenJ(apiClient);

        PushedTransaction pushedTransaction = null;
        try {
            EnumSet<YosemiteTokenJ.CanSetOptionsType> emptyOptions = EnumSet.noneOf(YosemiteTokenJ.CanSetOptionsType.class);
            pushedTransaction = yxTokenJ.createToken("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, emptyOptions, null).join();
            log("Create Transaction:" + pushedTransaction.getTransactionId());
        } catch (Exception e) {
            log(e.toString());
        }

        pushedTransaction = yxTokenJ.issueToken("user1", "1.23456789 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo", null).join();
        log("Issue Transaction:" + pushedTransaction.getTransactionId());

        // transfer token with transacation fee payer as TOKEN_PROVIDER_ACCOUNT
        pushedTransaction = yxTokenJ.transferTokenWithPayer("user1", TOKEN_PROVIDER_ACCOUNT, "1.12345678 XYZ", TOKEN_PROVIDER_ACCOUNT,
                TOKEN_PROVIDER_ACCOUNT, "my memo", null).join();
        log("TransferWithPayer Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = yxTokenJ.redeemToken("1.12345678 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo", null).join();
        log("Redeem Transaction:" + pushedTransaction.getTransactionId());

        if (!wait_for_irreversibility) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignored
            }
        }

        TableRow tableRow = yxTokenJ.getTokenStats("XYZ", 8, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "user1").join();
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

    private static void prepareTokenProvider(YosemiteApiRestClient apiClient) {
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        // create the key pair of the service provider and create its account
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, TOKEN_PROVIDER_ACCOUNT);
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        // issue native token by system depository
        YosemiteNativeTokenJ nativeTokenJ = new YosemiteNativeTokenJ(apiClient);
        PushedTransaction pushedTransaction = nativeTokenJ.issueNativeToken(TOKEN_PROVIDER_ACCOUNT, "1000000.0000 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null).join();
        log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());
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
