package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.StandardToken;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;

import java.util.EnumSet;

/**
 * @author Eugene Chung
 */
abstract class SampleCommon {
    static void waitForIrreversibility(YosemiteApiRestClient apiRestClient, PushedTransaction pushedTransaction) {
        int waitTime = apiRestClient.getTxExpirationInMillis() + 10000; // + 10 seconds
        do {
            Transaction tx = apiRestClient.getTransaction(pushedTransaction.getTransactionId()).execute();
            if (tx.getIrreversibleAt() != null) {
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

    static void processKYC(YosemiteSystemJ yxSystemJ, String identityAuthorityAccount,
                           String accountName, EnumSet<KYCStatusType> flags) {
        String contract = "yx.identity";
        String action = "setidinfo";
        String data = "{\"identity_authority\":\"" + identityAuthorityAccount + "\",\"account\":\"" + accountName + "\",\"type\":0,\"kyc\":" + KYCStatusType.getAsBitFlags(flags) + ",\"state\":0,\"data\":\"\"}";
        TransactionParameters txParameters =
                TransactionParameters.Builder().addPermission(identityAuthorityAccount).setTransactionFeePayer(identityAuthorityAccount).build();

        PushedTransaction pushedTransaction = yxSystemJ.pushAction(contract, action, data, txParameters).join();
        log("\nsetidinfo Transaction:\n" + pushedTransaction.getTransactionId());
    }

    static void prepareServiceProvider(YosemiteApiRestClient apiClient, String idAuthAccount, String systemTokenAccount, String accountName) {
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        // create the key pair of the service provider and create its account
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, idAuthAccount, accountName);
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        // KYC process done by Identity Authority Service for DKRW
        processKYC(yxSystemJ, idAuthAccount, accountName, EnumSet.allOf(KYCStatusType.class));

        // issue native token by system depository
        StandardToken systemToken = new StandardToken(apiClient);
        PushedTransaction pushedTransaction = systemToken.issueToken(accountName, "100000000.0000 DUSD", systemTokenAccount, "", null).join();
        log("Issue System Token Transaction : " + pushedTransaction.getTransactionId());
    }

    static String createKeyPairAndAccount(YosemiteApiRestClient apiClient, YosemiteSystemJ yxSystemJ,
                                          String creator, String accountName) {
        String publicKey = apiClient.createKey().execute();
        PushedTransaction pushedTransaction = yxSystemJ.createAccount(
                creator, accountName, publicKey, publicKey, null).join();
        log("Account Creation Transaction : " + pushedTransaction.getTransactionId());
        return publicKey;
    }

    static void log(String s) {
        System.out.println(s);
    }
}
