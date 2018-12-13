import com.google.gson.Gson;
import io.yosemite.crypto.ec.EcDsa;
import io.yosemite.data.remote.api.AbiBinToJsonRequest;
import io.yosemite.data.remote.api.AbiBinToJsonResponse;
import io.yosemite.data.remote.chain.*;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;
import io.yosemite.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

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
    public void tesetCreateAccountTest() {

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
        System.out.println(Utils.prettyPrintJson(result));
    }

    //@Test
    public void testGetAccount() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Account result = apiClient.getAccount("yosemite").execute();
        System.out.println(Utils.prettyPrintJson(result));
    }

    @Test
    public void testGetTransaction() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        Transaction result = apiClient.getTransaction("b7c7ab7aeaea85ae5e84db798904eea7ce1cf79dccd5f2e9414286ebd6c020b8").execute();
        logger.debug(Utils.prettyPrintJson(result));

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
        logger.debug(Utils.prettyPrintJson(actions));
    }

    //@Test
    public void testAbiBinToJson() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        AbiBinToJsonRequest req = new AbiBinToJsonRequest("yx.token", "entrustui", "10aeca58e5740df2a090db57e1740df20243524400000000a090db57e1740df2");
        AbiBinToJsonResponse response = apiClient.abiBinToJson(req).execute();
        logger.debug(Utils.prettyPrintJson(response));
    }

    //@Test
    public void testYosemiteNativeTokenJ() {

        final String chainId = "6376573815dbd2de2d9929027a94aeab3f6e60e87caa953f94ee701ac8425811";

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        apiClient.setTransactionVoteTarget("d1");
        apiClient.setDelegatedTransactionFeePayer("payeraccount");

            Account serviceUser = apiClient.getAccount("serviceuser1").execute();
        Account userAccount = apiClient.getAccount("useraccounta").execute();
        Account payerAccount = apiClient.getAccount("payeraccount").execute();

        String contract = "yx.ntoken";
        String action = "transfer";
        String data = "{\"from\":\"" + serviceUser.getAccountName() + "\",\"to\":\"" + userAccount.getAccountName() + "\",\"amount\":\"1.00 DKRW\",\"memo\":\"test\"}";

        YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);

        TransactionParameters txParameters = TransactionParameters.Builder().
                addPermission(serviceUser.getAccountName()).
                addPublicKey(serviceUser.getActivePublicKey()).
                build();
        final SignedTransaction signedTransactionByService = yxj.signTransaction(
                contract, action , data, txParameters).join();

        logger.debug("\nFirst Signed Transaction:\n" + Utils.prettyPrintJson(signedTransactionByService));

        Gson gson = Utils.createYosemiteJGsonBuilder().create();

        String stringifiedSignedTransactionByService = gson.toJson(signedTransactionByService);

        // Pass along the signed transaction in JSON string to the next party to get the signature from

        final SignedTransaction unmarshalledTransaction = gson.fromJson(stringifiedSignedTransactionByService, SignedTransaction.class);

        final SignedTransaction finalSignedTransaction = yxj.signTransaction(
                unmarshalledTransaction, chainId, Collections.singletonList(payerAccount.getActivePublicKey())).join();

        logger.debug("\nFinal Signed Transaction:\n" + Utils.prettyPrintJson(finalSignedTransaction));

        PushedTransaction pushedTransaction = apiClient.pushTransaction(new PackedTransaction(finalSignedTransaction)).execute();

        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));

        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }

}
