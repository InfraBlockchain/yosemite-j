import com.google.gson.Gson;
import io.yosemite.Consts;
import io.yosemite.services.yxcontracts.*;
import io.yosemite.crypto.ec.EcDsa;
import io.yosemite.data.remote.api.AbiBinToJsonRequest;
import io.yosemite.data.remote.api.AbiBinToJsonResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.data.types.TypeAuthority;
import io.yosemite.services.*;
import io.yosemite.util.Utils;
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
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TransactionParameters txParam = TransactionParameters.Builder().
            addPublicKey("YOS58dGg2iHMve2NgkyjXMtcBEX7785HnQqnA7kyFHCDwLNw1mD2B"). //public key of yosemite
            addPublicKey("YOS6EfGUaA5MNLH1GiHd64DcDr3HMgY1AM3WR1vdHKaah9Z4cWPZq"). //public key of idauth.a
            setTransactionFeePayer("yosemite").
            build();
        PushedTransaction pushedTransaction = yxj.createAccount("idauth.a", "joepark1good",
                "YOS8mwHZCH4e8mXsAzfhAebagsaoCDLN5d7nenkwfMcQtWrZeZdvp",
                "YOS6pR7dfCkMkuEePpLs3bJxt39eE8qb2hVNWmv93jFHEMQbTRRsJ",
                txParam
        ).join();

        logger.debug(pushedTransaction.getTransactionId());
    }

    //@Test
    public void testSimpleChangeActivePermissionTest() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority authority = TypeAuthority.Builder().addPublicKey("YOS8NJdBr8ir5cXULvKf3sk4gLzQuecRXaKiMbjEpTriLZ8AAsJZW").build();
        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission("joepark1good", "owner").
            addPublicKey("YOS58dGg2iHMve2NgkyjXMtcBEX7785HnQqnA7kyFHCDwLNw1mD2B"). //public key of yosemite
            addPublicKey("YOS8mwHZCH4e8mXsAzfhAebagsaoCDLN5d7nenkwfMcQtWrZeZdvp"). //public key of owner
            setTransactionFeePayer("yosemite").
            build();
        PushedTransaction pushedTransaction = yxj.setAccountPermission("joepark1good", authority, txParam).join();
        logger.debug("updateauth : " + pushedTransaction.getTransactionId());
    }

    //@Test
    public void testCreateAccountWithAthoritiesTest() {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority ownerAuthority = TypeAuthority.Builder().
            setThreshold(1).
            addAccount("idauth.a"). //public key of idauth.a
            addPublicKey("YOS79FWgriJiu1JAWARVZDNaqDZnVKnPW2gQn1N9Ne6cNPf8cA8Nj").
            build();
        TypeAuthority activeAuthority = TypeAuthority.Builder().addPublicKey("YOS79FWgriJiu1JAWARVZDNaqDZnVKnPW2gQn1N9Ne6cNPf8cA8Nj").build();
        TransactionParameters parameters = TransactionParameters.Builder().
            addPublicKey("YOS58dGg2iHMve2NgkyjXMtcBEX7785HnQqnA7kyFHCDwLNw1mD2B"). //public key of yosemite
            addPublicKey("YOS6EfGUaA5MNLH1GiHd64DcDr3HMgY1AM3WR1vdHKaah9Z4cWPZq"). //public key of idauth.a
            setTransactionFeePayer("yosemite").
            build();
        PushedTransaction pushedTransaction = yxj.createAccount("idauth.a", "joepark2good", ownerAuthority, activeAuthority, parameters).join();

        logger.debug(pushedTransaction.getTransactionId());
    }

    //@Test
    public void testChangeActivePermissionKeyForAthoritiesTest() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority activeAuthority = TypeAuthority.Builder().addPublicKey("YOS8NJdBr8ir5cXULvKf3sk4gLzQuecRXaKiMbjEpTriLZ8AAsJZW").build();
        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission("joepark2good", "owner").
            addPublicKey("YOS58dGg2iHMve2NgkyjXMtcBEX7785HnQqnA7kyFHCDwLNw1mD2B"). //public key of yosemite
            addPublicKey("YOS6EfGUaA5MNLH1GiHd64DcDr3HMgY1AM3WR1vdHKaah9Z4cWPZq"). //public key of idauth.a
            setTransactionFeePayer("yosemite").
            build();

        PushedTransaction pushedTransaction = yxj.setAccountPermission("joepark2good", activeAuthority, txParam).join();
        logger.debug("updateauth for active : " + pushedTransaction.getTransactionId());

        TypeAuthority ownerAuthority = TypeAuthority.Builder().
            setThreshold(1).
            addAccount("idauth.a"). //public key of idauth.a
            addPublicKey("YOS8NJdBr8ir5cXULvKf3sk4gLzQuecRXaKiMbjEpTriLZ8AAsJZW").
            build();
        PushedTransaction pushedTransaction2 = yxj.setAccountPermission("joepark2good", "owner", "", ownerAuthority, txParam).join();
        logger.debug("updateauth for owner : " + pushedTransaction2.getTransactionId());

        // the same operation above with one transaction including multi-actions

        String updateAuthData1 =
            ActionDataJsonCreator.updateAuth("joepark2good", Consts.ACTIVE_PERMISSION_NAME, Consts.OWNER_PERMISSION_NAME, activeAuthority);
        String updateAuthData2 =
            ActionDataJsonCreator.updateAuth("joepark2good", Consts.OWNER_PERMISSION_NAME, "", ownerAuthority);

        ActionSpecifier actionPair1 = new ActionSpecifier(Consts.YOSEMITE_SYSTEM_CONTRACT, YosemiteSystemConsts.ACTION_UPDATE_AUTH, updateAuthData1);
        ActionSpecifier actionPair2 = new ActionSpecifier(Consts.YOSEMITE_SYSTEM_CONTRACT, YosemiteSystemConsts.ACTION_UPDATE_AUTH, updateAuthData2);

        pushedTransaction =
            yxj.pushActions(Stream.of(actionPair1, actionPair2).collect(Collectors.toList()), txParam).join();
        logger.debug("\nPushed Transaction:\n" + Utils.toJson(pushedTransaction, true));
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
    public void testCreateAccountPermissionTest() {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient("http://127.0.0.1:8888", "http://127.0.0.1:8900");

        YosemiteSystemJ yxj = new YosemiteSystemJ(apiClient);
        TypeAuthority authority = TypeAuthority.Builder().addAccount("ycard.cusd.a").build();

        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission("user1").
            setTransactionFeePayer("yosemite").
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
            setTransactionFeePayer("yosemite").
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

        Actions actions = apiClient.getActions("ysmt.dusd.a", -1, -50).execute();
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
        apiClient.setTransactionFeePayer("payeraccount");

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

    //http://testnet-sentinel-explorer.yosemitelabs.org/transactions/1755765/1a37ce7e2cea145145d1b7662ec640a9ab7180869067e13d647ed9e500e6b98d
    //@Test
    public void testPushActions() {
        String TOKEN_ISSUER_NAME = "ysmt.dusd.a";
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
            "http://testnet-sentinel.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-sentinel-explorer-api.yosemitelabs.org");
        StandardToken standardToken = new StandardToken(apiClient);

        String transferData1 =
            ActionDataJsonCreator.transferToken(TOKEN_ISSUER_NAME, "producer.a", "1.0000 DUSD", TOKEN_ISSUER_NAME, "tag1");
        String transferData2 =
            ActionDataJsonCreator.transferToken(TOKEN_ISSUER_NAME, "producer.b", "2.0000 DUSD", TOKEN_ISSUER_NAME, "tag2");

        ActionSpecifier actionPair1 = new ActionSpecifier(TOKEN_ISSUER_NAME, StandardTokenConsts.ACTION_TRANSFER, transferData1);
        ActionSpecifier actionPair2 = new ActionSpecifier(TOKEN_ISSUER_NAME, StandardTokenConsts.ACTION_TRANSFER, transferData2);

        TransactionParameters txParam = TransactionParameters.Builder().
            addPermission(TOKEN_ISSUER_NAME).
            setTransactionFeePayer(TOKEN_ISSUER_NAME).
            build();

        PushedTransaction pushedTransaction =
            standardToken.pushActions(Stream.of(actionPair1, actionPair2).collect(Collectors.toList()), txParam).join();
        logger.debug("\nPushed Transaction:\n" + Utils.toJson(pushedTransaction, true));
    }
}
