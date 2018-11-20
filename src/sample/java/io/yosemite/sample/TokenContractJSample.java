package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.services.CommonParameters;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;
import io.yosemite.services.yxcontracts.YosemiteTokenJ;

import java.util.EnumSet;
import java.util.Map;

public class TokenContractJSample extends SampleCommon {
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
                    prepareServiceProvider(apiClient, SYSTEM_DEPOSITORY_ACCOUNT, TOKEN_PROVIDER_ACCOUNT);
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
        log("Token provider's public key = " + tokenProviderPublicKey);

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

        YosemiteNativeTokenJ nativeTokenJ = new YosemiteNativeTokenJ(apiClient);
        PushedTransaction pushedTransaction = nativeTokenJ.issueNativeToken(
                "tkuserxxxxx1", "1000000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null).join();
        log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());

        YosemiteTokenJ yxTokenJ = new YosemiteTokenJ(apiClient);
        CommonParameters commonParametersForTokenProvider =
                CommonParameters.Builder().addPublicKey(tokenProviderPublicKey).build();
        CommonParameters commonParametersForUser1 =
                CommonParameters.Builder().addPublicKey(tokenUser1PublicKey).build();

        try {
            EnumSet<YosemiteTokenJ.CanSetOptionsType> emptyOptions = EnumSet.noneOf(YosemiteTokenJ.CanSetOptionsType.class);
            pushedTransaction = yxTokenJ.createToken("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, emptyOptions,
                    commonParametersForTokenProvider).join();
            log("Create Transaction:" + pushedTransaction.getTransactionId());
        } catch (Exception e) {
            log(e.toString()); // already created
        }

        pushedTransaction = yxTokenJ.issueToken("tkuserxxxxx1", "1.23456789 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo",
                commonParametersForTokenProvider).join();
        log("Issue Transaction:" + pushedTransaction.getTransactionId());

        log("");
        log("[Token Stats]");
        TableRow tableRow = yxTokenJ.getTokenStats("XYZ", 8, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Account's Token Balance]");
        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.transferToken("tkuserxxxxx1", TOKEN_PROVIDER_ACCOUNT, "1.23456789 XYZ",
                TOKEN_PROVIDER_ACCOUNT, "my memo", commonParametersForUser1).join();
        log("Transfer Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        pushedTransaction = yxTokenJ.redeemToken("1.23456789 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo",
                commonParametersForTokenProvider).join();
        log("Redeem Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        log("");
        log("[Token Stats]");
        tableRow = yxTokenJ.getTokenStats("XYZ", 8, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Account's Token Balance]");
        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.setUserIssueLimit("tkuserxxxxx1", "50.00000000 XYZ", TOKEN_PROVIDER_ACCOUNT,
                commonParametersForTokenProvider).join();
        log("Grant Issue Authority Transaction:" + pushedTransaction.getTransactionId());

        pushedTransaction = yxTokenJ.issueTokenByUser("tkuserxxxxx1", "tkuserxxxxx1", "10.12345678 XYZ",
                TOKEN_PROVIDER_ACCOUNT, "my memo", commonParametersForUser1).join();
        log("Issue By User Transaction:" + pushedTransaction.getTransactionId());

        log("");
        log("[Account's Delegated Issue]");
        tableRow = yxTokenJ.getTokenDelegatedIssue("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Token Stats]");
        tableRow = yxTokenJ.getTokenStats("XYZ", 8, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Account's Token Balance]");
        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        // transfer token from user to issuer for redemption
        pushedTransaction = yxTokenJ.transferToken("tkuserxxxxx1", TOKEN_PROVIDER_ACCOUNT, "10.12345678 XYZ",
                TOKEN_PROVIDER_ACCOUNT, "my memo", commonParametersForUser1).join();
        log("Transfer Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        try {
            pushedTransaction = yxTokenJ.entrustUserIssueTo("tkuserxxxxx1", TOKEN_PROVIDER_ACCOUNT,
                    "XYZ", 8, TOKEN_PROVIDER_ACCOUNT, commonParametersForUser1).join();
            log("entrustUserIssueTo Transaction:" + pushedTransaction.getTransactionId());
            if (wait_for_irreversibility) {
                waitForIrreversibility(apiClient, pushedTransaction);
            }
        } catch (Exception e) {
            log(e.toString()); // entrusted issuer is already set
        }

        log("");
        log("[Account's Delegated Issue]");
        tableRow = yxTokenJ.getTokenDelegatedIssue("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.issueTokenByUser("tkuserxxxxx1", TOKEN_PROVIDER_ACCOUNT,
                "1.00000000 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo",
                commonParametersForTokenProvider).join();
        log("Issue By User Transaction:" + pushedTransaction.getTransactionId());

        log("");
        log("[Account's Delegated Issue]");
        tableRow = yxTokenJ.getTokenDelegatedIssue("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Account's Token Balance]");
        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.redeemToken("11.12345678 XYZ", TOKEN_PROVIDER_ACCOUNT, "my memo",
                commonParametersForTokenProvider).join();
        log("Redeem Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        log("");
        log("[Token Stats]");
        tableRow = yxTokenJ.getTokenStats("XYZ", 8, TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Account's Token Balance]");
        tableRow = yxTokenJ.getTokenAccountBalance("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.changeIssuedTokenAmount("tkuserxxxxx1", "10.12345678 XYZ", TOKEN_PROVIDER_ACCOUNT, true,
                commonParametersForTokenProvider).join();
        log("Issue By User Transaction:" + pushedTransaction.getTransactionId());

        log("");
        log("[Account's Delegated Issue]");
        tableRow = yxTokenJ.getTokenDelegatedIssue("XYZ", 8, TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }
    }

}
