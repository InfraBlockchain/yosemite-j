import com.google.gson.Gson;
import io.yosemite.StandardTokenConsts;
import io.yosemite.crypto.ec.EcDsa;
import io.yosemite.data.remote.api.AbiBinToJsonRequest;
import io.yosemite.data.remote.api.AbiBinToJsonResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.services.yxcontracts.StandardToken;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;
import io.yosemite.util.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class LibraryTest {

    final static Logger logger = LoggerFactory.getLogger(LibraryTest.class);

    //@Test
    public void walletTest() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        apiClient.createKey("default", "k1").execute();
    }

    //@Test
    public void testSignDigestAndVerify() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        String strData = "hello";
        byte[] data = strData.getBytes(StandardCharsets.UTF_8);
        String pubKey = "YOS6pR7dfCkMkuEePpLs3bJxt39eE8qb2hVNWmv93jFHEMQbTRRsJ";

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        String signature = yxj.sign(data, pubKey).join();

        logger.info("Data to sign: " + strData);
        logger.info("Public Key corresponding to a private key to sign: " + pubKey);
        logger.info("Signature: " + signature);

        Assert.assertTrue(EcDsa.verifySignature(data, signature, pubKey));
    }

    //@Test
    public void testCreateAccountTest() {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        PushedTransaction pushedTransaction = yxj.createAccount("idauth1", "joepark1good",
                "YOS6pR7dfCkMkuEePpLs3bJxt39eE8qb2hVNWmv93jFHEMQbTRRsJ",
                "YOS6pR7dfCkMkuEePpLs3bJxt39eE8qb2hVNWmv93jFHEMQbTRRsJ",
                null
        ).join();

        logger.debug(pushedTransaction.getTransactionId());
    }

    //@Test
    public void testCreateAccountWithAthoritiesTest() {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority ownerAuthority =
                TypeAuthority.Builder().
                        setThreshold(1).
                        addPublicKey("YOS79FWgriJiu1JAWARVZDNaqDZnVKnPW2gQn1N9Ne6cNPf8cA8Nj").
                        addAccount("d1").
                        build();
        TypeAuthority activeAuthority = TypeAuthority.Builder().addPublicKey("YOS79FWgriJiu1JAWARVZDNaqDZnVKnPW2gQn1N9Ne6cNPf8cA8Nj").build();
        PushedTransaction pushedTransaction = yxj.createAccount("d1", "joeparkygood", ownerAuthority, activeAuthority, null).join();

        logger.debug(pushedTransaction.getTransactionId());
    }

    /*
push action yosemite updateauth '{"account":"user1","permission":"creditissue","parent":"active",
                                                "auth":{"threshold":1,"keys":[],"waits":[],
                                                "accounts":[{"weight":1,"permission":{"actor":"ycard.cusd.a","permission":"active"}}]}}'
              -p user1@active --txfee-payer ycard.cusd.a
push action yosemite linkauth '["user1","ycard.cusd.a","creditissue","creditissue"]' -p user1@active --txfee-payer ycard.cusd.a

push action ycard.cusd.a creditissue '["user1","ycard.cusd.a","500.0000 CUSD",""]' -p useraccounta@creditissue --txfee-payer ycard.cusd.a
     */
    
    //@Test
    public void testSetAccountPermissionTest() {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority authority = TypeAuthority.Builder().addAccount("ycard.cusd.a").build();

        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission("user1").
            setDelegatedTransactionFeePayer("yosemite").
            build();
        PushedTransaction pushedTransaction2 = yxj.setAccountPermission("user1", "creditissue", "active", authority, txParam).join();
        logger.debug("updateauth : " + pushedTransaction2.getTransactionId());
    }

    //@Test
    public void testLinkPermissionTest() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TransactionParameters txParams = TransactionParameters.Builder().
            addPermission("user1").
            setDelegatedTransactionFeePayer("yosemite").
            build();
        PushedTransaction pushedTransaction = yxj.linkPermission("user1", "ycard.cusd.a", "creditissue", "creditissue", txParams).join();
        logger.debug("linkauth : " + pushedTransaction.getTransactionId());
    }

    //@Test
    public void testGetInfo() throws IOException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Info result = apiClient.getInfo().execute();
        System.out.println(result);
    }

    //@Test
    public void testGetBlock() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Block result = apiClient.getBlock("74187").execute();
        System.out.println(Utils.toJson(result, true));
    }

    //@Test
    public void testGetAccount() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Account result = apiClient.getAccount("yosemite").execute();
        System.out.println(Utils.toJson(result, true));
    }

    //@Test
    public void testGetTransaction() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        Transaction result = apiClient.getTransaction("6100970467e5d68b6cd588f0f6cbffc0b78204bdaa1a7544ffad000b348fec71").execute();
        logger.debug(Utils.toJson(result, true));

        if (result.getIrreversibleAt() != null) {
            logger.debug("Irreversible block timestamp at : " + result.getIrreversibleAt().getTimestamp());
        } else {
            logger.debug("Not irreversible yet");
        }
    }

    //@Test
    public void testGetActions() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        Actions actions = apiClient.getActions("rentservice1", -1, -50).execute();
        logger.debug("Last irreversible block: " + actions.getLastIrreversibleBlock());
        logger.debug(Utils.toJson(actions, true));
    }

    //@Test
    public void testAbiBinToJson() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        AbiBinToJsonRequest req = new AbiBinToJsonRequest("yx.token", "entrustui", "10aeca58e5740df2a090db57e1740df20243524400000000a090db57e1740df2");
        AbiBinToJsonResponse response = apiClient.abiBinToJson(req).execute();
        logger.debug(Utils.toJson(response, true));
    }

    //@Test
    public void testYosemiteNativeTokenJ() {

        final String chainId = "047316f411b2db9ba0f600fdbca8e3bbd224d82a367ff02fbd355bb0675288e3";

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-sentinel-explorer-api.yosemitelabs.org");

        apiClient.setTransactionVoteTarget("producer.a");
        apiClient.setDelegatedTransactionFeePayer("payeraccount");

        Account serviceUser = apiClient.getAccount("serviceuser1").execute();
        Account userAccount = apiClient.getAccount("useraccounta").execute();
        Account payerAccount = apiClient.getAccount("payeraccount").execute();

        String contract = "systoken.a";
        String action = "transfer";
        String data = "{\"t\":\"" + contract + "\",\"from\":\"" + serviceUser.getAccountName() + "\",\"to\":\"" + userAccount.getAccountName() + "\",\"amount\":\"1.0000 DUSD\",\"memo\":\"test\"}";

        YosemiteJ yxj = new StandardToken(apiClient);

        TransactionParameters txParameters = TransactionParameters.Builder().
                addPermission(serviceUser.getAccountName()).
                addPublicKey(serviceUser.getActivePublicKey()).
                build();
        final SignedTransaction signedTransactionByService = yxj.signTransaction(
                contract, action , data, txParameters).join();

        logger.debug("\nFirst Signed Transaction:\n" + Utils.toJson(signedTransactionByService, true));

        Gson gson = Utils.createYosemiteJGson();

        String stringifiedSignedTransactionByService = gson.toJson(signedTransactionByService);

        // Pass along the signed transaction in JSON string to the next party to get the signature from

        final SignedTransaction unmarshalledTransaction = gson.fromJson(stringifiedSignedTransactionByService, SignedTransaction.class);

        final SignedTransaction finalSignedTransaction = yxj.signTransaction(
                unmarshalledTransaction, chainId, Collections.singletonList(payerAccount.getActivePublicKey())).join();

        logger.debug("\nFinal Signed Transaction:\n" + Utils.toJson(finalSignedTransaction, true));

        PushedTransaction pushedTransaction = apiClient.pushTransaction(new PackedTransaction(finalSignedTransaction)).execute();

        logger.debug("\nPushed Transaction:\n" + Utils.toJson(pushedTransaction, true));

        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }

    //http://testnet-sentinel-explorer.yosemitelabs.org/transactions/1755765/338344d0be71377031172b7e08a2251d8c1d3817b400d95d309f7365b90f3c0a
    //@Test
    public void testPushActions() {
        String TOKEN_ISSUER_NAME = "ysmt.dusd.a";
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
            "http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-sentinel-explorer-api.yosemitelabs.org");
        StandardToken standardToken = new StandardToken(apiClient);

        String transferData1 =
            standardToken.getTransferTokenJsonString(TOKEN_ISSUER_NAME, TOKEN_ISSUER_NAME, "producer.a", "1.0000 DUSD", "tag1");
        String transferData2 =
            standardToken.getTransferTokenJsonString(TOKEN_ISSUER_NAME, TOKEN_ISSUER_NAME, "producer.b", "2.0000 DUSD", "tag2");

        ImmutablePair<String, String> actionPair1 = new ImmutablePair<>(StandardTokenConsts.ACTION_TRANSFER, transferData1);
        ImmutablePair<String, String> actionPair2 = new ImmutablePair<>(StandardTokenConsts.ACTION_TRANSFER, transferData2);

        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission(TOKEN_ISSUER_NAME).
            setDelegatedTransactionFeePayer(TOKEN_ISSUER_NAME).
            build();

        PushedTransaction pushedTransaction =
            standardToken.pushActions(TOKEN_ISSUER_NAME, Stream.of(actionPair1, actionPair2).collect(Collectors.toList()), txParam).join();
        logger.debug("\nPushed Transaction:\n" + Utils.toJson(pushedTransaction, true));
    }
}
