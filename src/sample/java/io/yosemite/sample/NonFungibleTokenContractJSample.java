package io.yosemite.sample;

import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.yxcontracts.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class NonFungibleTokenContractJSample extends SampleCommon {
    private static final String SYSTEM_DEPOSITORY_ACCOUNT = "d1";
    private static final String TOKEN_PROVIDER_ACCOUNT = "gameprovider";

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        // Create Yosemite Client with servers of the same machine; transaction vote target for PoT is set to "d1",
        // which wants to become a block producer
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
                "tkuserxxxxx1", "1000000.00 DKRW", SYSTEM_DEPOSITORY_ACCOUNT, "", null, null).join();
        log("Issue Native Token Transaction : " + pushedTransaction.getTransactionId());

        YosemiteNonFungibleTokenJ yxTokenJ = new YosemiteNonFungibleTokenJ(apiClient);

        try {
            EnumSet<YosemiteTokenJ.CanSetOptionsType> emptyOptions = EnumSet.noneOf(YosemiteTokenJ.CanSetOptionsType.class);
            pushedTransaction = yxTokenJ.createToken("MYITEM", TOKEN_PROVIDER_ACCOUNT, emptyOptions,
                    null, new String[]{tokenProviderPublicKey}).join();
            log("Create Transaction:" + pushedTransaction.getTransactionId());
        } catch (Exception e) {
            log(e.toString()); // already created
        }

        List<Long> ids = new ArrayList<>();
        ids.add(1000L);
        ids.add(1001L);

        try {
            pushedTransaction = yxTokenJ.redeemToken(TOKEN_PROVIDER_ACCOUNT, ids, "my memo", null, new String[]{tokenProviderPublicKey}).join();
            log("Redeem Transaction:" + pushedTransaction.getTransactionId());
            if (!wait_for_irreversibility) {
                waitForIrreversibility(apiClient, pushedTransaction);
            }
        } catch (Exception e) {
            log(e.toString()); // NFT not found
        }

        List<String> uris = new ArrayList<>();
        uris.add("http://game.com/git0");
        uris.add("http://game.com/git1");
        pushedTransaction = yxTokenJ.issueToken("tkuserxxxxx1", "MYITEM", TOKEN_PROVIDER_ACCOUNT, ids, uris, "swordX",
                "my memo", null, new String[]{tokenProviderPublicKey}).join();
        log("Issue Transaction:" + pushedTransaction.getTransactionId());

        TableRow tableRow = yxTokenJ.getTokenStats("MYITEM", TOKEN_PROVIDER_ACCOUNT).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        tableRow = yxTokenJ.getTokenAccountBalance("MYITEM", TOKEN_PROVIDER_ACCOUNT, "tkuserxxxxx1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        tableRow = yxTokenJ.getTokenById(TOKEN_PROVIDER_ACCOUNT, 1000L).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        pushedTransaction = yxTokenJ.transferByTokenId("tkuserxxxxx1", TOKEN_PROVIDER_ACCOUNT, TOKEN_PROVIDER_ACCOUNT, ids, "transfer for redeem",
                null, new String[]{tokenUser1PublicKey}).join();
        log("TransferId Transaction:" + pushedTransaction.getTransactionId());

        pushedTransaction = yxTokenJ.redeemToken(TOKEN_PROVIDER_ACCOUNT, ids, "my memo", null, new String[]{tokenProviderPublicKey}).join();
        log("Redeem Transaction:" + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }
    }

}
