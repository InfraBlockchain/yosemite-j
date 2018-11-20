package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.YosemiteDigitalContractJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;

import java.util.*;

public class DigitalContractJSample extends SampleCommon {
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";
    private static final String SERVICE_PROVIDER_ACCOUNT = "servprovider";
    private static final String USER1_ACCOUNT = "servpuserxx1";
    private static final String USER2_ACCOUNT = "servpuserxx2";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        // Create Yosemite Client with servers of the same machine; transaction vote target for PoT is set to "d1"
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900");
        apiClient.setTransactionVoteTarget(SYSTEM_DEPOSITORY_ACCOUNT);

        if (args.length > 0) {
            for (String arg : args) {
                if ("-prepare".equals(arg)) {
                    prepareServiceProvider(apiClient, SYSTEM_DEPOSITORY_ACCOUNT, SERVICE_PROVIDER_ACCOUNT);
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
        Account account = apiClient.getAccount(SERVICE_PROVIDER_ACCOUNT).execute();
        String serviceProviderPublicKey = account.getActivePublicKey();

        // create the user accounts or get the public key from the chain
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        String user1PublicKey;
        try {
            user1PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, USER1_ACCOUNT);
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
            user1PublicKey = apiClient.getAccount(USER1_ACCOUNT).execute().getActivePublicKey();
        }

        String user2PublicKey;
        try {
            user2PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, USER2_ACCOUNT);
        } catch (Exception e) {
            log(e.toString());
            user2PublicKey = apiClient.getAccount(USER2_ACCOUNT).execute().getActivePublicKey();
        }

        // KYC process done by Identity Authority Service
        // assume d1 is Identity Authority and the users did phone authentication(2=KYCStatusType.KYC_STATUS_PHONE_AUTH) successfully
        processKYC(yxSystemJ, SYSTEM_DEPOSITORY_ACCOUNT, USER1_ACCOUNT, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH));
        processKYC(yxSystemJ, SYSTEM_DEPOSITORY_ACCOUNT, USER2_ACCOUNT, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH));

        //----------------------------------------------
        // Let's start to use digital contract service!
        //----------------------------------------------
        YosemiteDigitalContractJ digitalContractJ = new YosemiteDigitalContractJ(apiClient);

        TransactionParameters txParametersForServiceProvider =
                TransactionParameters.Builder().addPublicKey(serviceProviderPublicKey).build();

        // 0. remove digital contract first
        PushedTransaction pushedTransaction;
        try {
            pushedTransaction = digitalContractJ.removeDigitalContract(
                    SERVICE_PROVIDER_ACCOUNT, 20, txParametersForServiceProvider).join();
            log("\nPushed Remove Transaction:\n" + pushedTransaction.getTransactionId());
        } catch (Exception ignored) {
        }

        // 1. create digital contract
        List<String> signers = Arrays.asList(USER1_ACCOUNT, USER2_ACCOUNT);
        // prepare expiration time based on UTC time-zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR, 48);
        Date expirationTime = calendar.getTime();

        pushedTransaction = digitalContractJ.createDigitalContract(SERVICE_PROVIDER_ACCOUNT, 20, "test1234", "",
                signers, expirationTime, 0, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH), (short) 0,
                txParametersForServiceProvider).join();
        log("\nPushed Create Transaction:\n" + pushedTransaction.getTransactionId());

        // 3. sign contract by signers
        TransactionParameters txParametersForUser2 = TransactionParameters.Builder().
                addPermission(USER2_ACCOUNT).addPermission(SERVICE_PROVIDER_ACCOUNT).
                addPublicKey(user2PublicKey).addPublicKey(serviceProviderPublicKey).
                setDelegatedTransactionFeePayer(SERVICE_PROVIDER_ACCOUNT).
                build();
        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 20, USER2_ACCOUNT, "",
                txParametersForUser2).join();
        log("\nPushed Sign Transaction:\n" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        TransactionParameters txParametersForUser1 = TransactionParameters.Builder().
                addPermission(USER1_ACCOUNT).addPermission(SERVICE_PROVIDER_ACCOUNT).
                addPublicKey(user1PublicKey).addPublicKey(serviceProviderPublicKey).
                setDelegatedTransactionFeePayer(SERVICE_PROVIDER_ACCOUNT).
                build();
        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 20, USER1_ACCOUNT, "I am user1",
                txParametersForUser1).join();
        log("\nPushed Sign Transaction:\n" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        // update additional info
        pushedTransaction = digitalContractJ.updateAdditionalDocumentHash(SERVICE_PROVIDER_ACCOUNT, 20, "added after signing",
                txParametersForServiceProvider).join();
        log("\nPushed Transaction:\n" + pushedTransaction.getTransactionId());

        log("");
        log("[Digital Contract]");
        TableRow tableRow = digitalContractJ.getCreatedDigitalContract(SERVICE_PROVIDER_ACCOUNT, 20).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Digital Contract Signer Info : " + USER1_ACCOUNT + "]");
        TableRow signerInfoTable = digitalContractJ.getSignerInfo(USER1_ACCOUNT, SERVICE_PROVIDER_ACCOUNT, 20).join();
        for (Map<String, ?> row : signerInfoTable.getRows()) {
            // There must be only one row.
            log((String) row.get("signerinfo"));
        }
    }

}
