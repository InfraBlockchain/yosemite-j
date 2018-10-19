package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
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
        // Create Yosemite Client with servers of the same machine; transaction vote target for PoT is set to "d1"
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");
        apiClient.setTransactionVoteTarget(SYSTEM_DEPOSITORY_ACCOUNT);

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

        // [IMPORTANT NOTE ON PUBLIC KEY]
        // In most cases, the public keys of the accounts are already known to the service provider.
        // Before creating an account, you must create the key pair and must save the public key somewhere like RDB for the account creation.
        // For this sample, we get the public keys from the chain, but in real case, you should get them from the RDB.
        Account account = apiClient.getAccount(TOKEN_PROVIDER_ACCOUNT).execute();
        String tokenProviderPublicKey = account.getActivePublicKey();

        // create the user accounts
        String tokenUser1PublicKey;
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        try {
            tokenUser1PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1");
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
            tokenUser1PublicKey = apiClient.getAccount("tkuserxxxxx1").execute().getActivePublicKey();
        }

        YosemiteTokenJ yxTokenJ = new YosemiteTokenJ(apiClient);

        PushedTransaction pushedTransaction = null;
        try {
            EnumSet<YosemiteTokenJ.CanSetOptionsType> emptyOptions = EnumSet.noneOf(YosemiteTokenJ.CanSetOptionsType.class);
            pushedTransaction = yxTokenJ.createToken("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, emptyOptions,
                    null, new String[]{tokenProviderPublicKey}).join();
            log("Create Transaction:" + pushedTransaction.getTransactionId());
        } catch (Exception e) {
            log(e.toString()); // already created
        }

        pushedTransaction = yxTokenJ.issueToken("tkuserxxxxx1", "1.23456789 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo",
                null, new String[]{tokenProviderPublicKey}).join();
        log("Issue Transaction:" + pushedTransaction.getTransactionId());

        pushedTransaction = yxTokenJ.redeemToken("1.12345678 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo", null, new String[]{tokenProviderPublicKey}).join();
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

        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }
    }

    private static void waitForIrreversibility(YosemiteApiRestClient apiRestClient, PushedTransaction pushedTransaction) {
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

    private static void processKYC(YosemiteSystemJ yxSystemJ, String accountName, EnumSet<KYCStatusType> flags) {
        String contract = "yx.identity";
        String action = "setidinfo";
        String data = "{\"identity_authority\":\"" + SYSTEM_DEPOSITORY_ACCOUNT + "\",\"account\":\"" + accountName + "\",\"type\":0,\"kyc\":" + KYCStatusType.getAsBitFlags(flags) + ",\"state\":0,\"data\":\"\"}";
        String[] permissions = new String[]{SYSTEM_DEPOSITORY_ACCOUNT + "@active"};

        PushedTransaction pushedTransaction = yxSystemJ.pushAction(contract, action, data, permissions).join();
        log("\nsetidinfo Transaction:\n" + pushedTransaction.getTransactionId());
    }

    private static void prepareTokenProvider(YosemiteApiRestClient apiClient) {
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        // create the key pair of the service provider and create its account
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, "yosemite", TOKEN_PROVIDER_ACCOUNT);
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        // KYC process done by Identity Authority Service for DKRW
        processKYC(yxSystemJ, TOKEN_PROVIDER_ACCOUNT, EnumSet.allOf(KYCStatusType.class));

        // issue native token by system depository
        YosemiteNativeTokenJ nativeTokenJ = new YosemiteNativeTokenJ(apiClient);
        PushedTransaction pushedTransaction = nativeTokenJ.issueNativeToken(
                TOKEN_PROVIDER_ACCOUNT, "1000000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null, null).join();
        log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());
    }

    private static String createKeyPairAndAccount(YosemiteApiRestClient apiClient, YosemiteSystemJ yxSystemJ, String creator, String accountName) {
        String publicKey = apiClient.createKey().execute();
        PushedTransaction pushedTransaction = yxSystemJ.createAccount(
                creator, accountName, publicKey, publicKey, null, null).join();
        log("Account Creation Transaction : " + pushedTransaction.getTransactionId());
        return publicKey;
    }

    private static void log(String s) {
        System.out.println(s);
    }
}
