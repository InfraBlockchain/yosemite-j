package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.*;
import io.yosemite.services.yxcontracts.*;

import java.util.*;

public class DigitalContractJSample {
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";
    private static final String SERVICE_PROVIDER_ACCOUNT = "servprovider";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");

        if (args.length > 0) {
            for (String arg : args) {
                if ("-prepare".equals(arg)) {
                    prepareServiceProvider(apiClient);
                    return;
                } else if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        // create the user accounts
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, "user1");
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, "user2");
        } catch (Exception e) {
            log(e.toString());
        }

        // KYC process done by Identity Authority Service
        processKYC(yxSystemJ, "user1");
        processKYC(yxSystemJ, "user2");

        //----------------------------------------------
        // Let's start to use digital contract service!
        //----------------------------------------------
        YosemiteDigitalContractJ digitalContractJ = new YosemiteDigitalContractJ(apiClient);

        // 0. remove digital contract first
        PushedTransaction pushedTransaction;
        try {
            pushedTransaction = digitalContractJ.removeDigitalContract(SERVICE_PROVIDER_ACCOUNT, 11, null).join();
            log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());
        } catch (Exception ignored) {
        }

        // 1. create digital contract
        List<String> signers = Arrays.asList("user1", "user2");
        // prepare expiration time based on UTC time-zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR, 48);
        Date expirationTime = calendar.getTime();

        pushedTransaction = digitalContractJ.createDigitalContract(SERVICE_PROVIDER_ACCOUNT, 11, "test1234", "",
                signers, expirationTime, 0, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH), (short) 0, null).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());

        // 3. sign contract by signers
        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 11, "user2", "", new String[]{"user2@active", "servprovider@active"}).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 11, "user1", "I am user1", null).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        // update additional info
        pushedTransaction = digitalContractJ.updateAdditionalDocumentHash(SERVICE_PROVIDER_ACCOUNT, 11, "added after signing", null).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());

        if (!wait_for_irreversibility) {
            try {
                // sleep 1 sec just for single node irreversibility
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log(e.toString());
            }
        }

        log("");
        log("[Digital Contract]");
        TableRow tableRow = digitalContractJ.getCreatedDigitalContract(SERVICE_PROVIDER_ACCOUNT, 11).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Digital Contract Signer Info : user1]");
        TableRow signerInfoTable = digitalContractJ.getSignerInfo("user1", SERVICE_PROVIDER_ACCOUNT, 11).join();
        for (Map<String, ?> row : signerInfoTable.getRows()) {
            // There must be only one row.
            log((String) row.get("signerinfo"));
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

    private static void processKYC(YosemiteSystemJ yxSystemJ, String accountName) {
        String contract = "yx.identity";
        String action = "setidinfo";
        // assume d1 is Identity Authority and the 'accountName' did phone authentication(2=KYCStatusType.KYC_STATUS_PHONE_AUTH) successfully
        String data = "{\"identity_authority\":\"d1\",\"account\":\"" + accountName + "\",\"type\":0,\"kyc\":2,\"state\":0,\"data\":\"\"}";
        String[] permissions = new String[]{"d1@active"};

        PushedTransaction pushedTransaction = yxSystemJ.pushAction(contract, action, data, permissions).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());
    }

    private static void prepareServiceProvider(YosemiteApiRestClient apiClient) {
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        // create the key pair of the service provider and create its account
        try {
            createKeyPairAndAccount(apiClient, yxSystemJ, "yosemite", SERVICE_PROVIDER_ACCOUNT);
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
        }

        // issue native token by system depository
        YosemiteNativeTokenJ nativeTokenJ = new YosemiteNativeTokenJ(apiClient);
        PushedTransaction pushedTransaction = nativeTokenJ.issueNativeToken(SERVICE_PROVIDER_ACCOUNT, "1000000.0000 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null).join();
        log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());
    }

    private static void createKeyPairAndAccount(YosemiteApiRestClient apiClient, YosemiteSystemJ yxSystemJ, String creator, String accountName) {
        String publicKey = apiClient.createKey("default").execute();
        PushedTransaction pushedTransaction = yxSystemJ.createAccount(
                creator, accountName, publicKey, publicKey, null).join();
        log("Account Creation Transaction : " + pushedTransaction.getTransactionId());
    }

    private static void log(String s) {
        System.out.println(s);
    }
}
