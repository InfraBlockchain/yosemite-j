import com.google.gson.Gson;
import io.yosemite.crypto.ec.EcDsa;
import io.yosemite.data.remote.chain.Block;
import io.yosemite.data.remote.chain.Info;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.event.TxIrreversibilityResponse;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.OrderedActionResult;
import io.yosemite.data.remote.history.transaction.Timestamp;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.services.event.EventNotificationCallback;
import io.yosemite.services.event.YosemiteEventNotificationClient;
import io.yosemite.services.event.YosemiteEventNotificationClientFactory;
import io.yosemite.services.yxcontracts.*;
import io.yosemite.util.Consts;
import io.yosemite.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
                new String[]{"idauth1@active"}
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

    //@Test
    public void testGetTransaction() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://testnet-explorer-api.yosemitelabs.org");

        Transaction result = apiClient.getTransaction("19970219477b72edcc86f55985b46763a39648b369317b4c9c461a102ef4c86c").execute();
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
    public void testYosemiteNativeTokenJ() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        apiClient.setTransactionVoteTarget("d1");

        YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);

        String contract = "yx.ntoken";
        String action = "transfer";
        String data = "{\"from\":\"user1\",\"to\":\"user2\",\"amount\":\"2.0000 DKRW\",\"memo\":\"test\"}";
        String[] permissions = new String[]{"user1@active"};

        PushedTransaction pushedTransaction = yxj.pushAction(contract, action, data, permissions).join();

        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));

        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }

    //@Test
    public void testYosemiteTokenJ() throws InterruptedException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteTokenJ yxj = new YosemiteTokenJ(apiClient);

        EnumSet<YosemiteTokenJ.CanSetOptionsType> emptyOptions = EnumSet.noneOf(YosemiteTokenJ.CanSetOptionsType.class);
        PushedTransaction pushedTransaction = yxj.createToken("TEST", 5, "d2", emptyOptions, new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        pushedTransaction = yxj.issueToken("user1", "100000.00000 TEST", "d2", "my memo", new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        pushedTransaction = yxj.transferToken("user1", "d2", "10000.00000 TEST", "d2", "my memo", new String[]{"user1@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        pushedTransaction = yxj.transferTokenWithPayer("user1", "d2", "10000.00000 TEST", "d2", "servprovider", "my memo",
                new String[]{"user1@active", "servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        pushedTransaction = yxj.redeemToken("20000.00000 TEST", "d2", "my memo", new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        TableRow tableRow = yxj.getTokenStats("TEST", 5, "d2").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            logger.debug(row.toString());
        }

        tableRow = yxj.getTokenAccountBalance("TEST", 5, "d2", "user1").join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            logger.debug(row.toString());
        }
    }

    //@Test
    public void testYosemiteTokenManagement() throws InterruptedException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteTokenJ yxj = new YosemiteTokenJ(apiClient);

        EnumSet<YosemiteTokenJ.CanSetOptionsType> allOptions = EnumSet.allOf(YosemiteTokenJ.CanSetOptionsType.class);
        PushedTransaction pushedTransaction = yxj.createToken("XYZ", 4, "d2", allOptions, new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        EnumSet<KYCStatusType> kycStatusPhoneAuth = EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH);
        pushedTransaction = yxj.setTokenKYCRule("XYZ", 4, "d2", YosemiteTokenJ.TokenRuleType.KYC_RULE_TRANSFER_RECEIVE, kycStatusPhoneAuth, new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        EnumSet<YosemiteTokenJ.TokenOptionsType> freezeTokenTransfer = EnumSet.of(YosemiteTokenJ.TokenOptionsType.FREEZE_TOKEN_TRANSFER);
        pushedTransaction = yxj.setTokenOptions("XYZ", 4, "d2", freezeTokenTransfer, true, new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        List<String> accounts = Arrays.asList("user1", "user2");
        pushedTransaction = yxj.freezeAccounts("XYZ", 4, "d2", accounts, true, new String[]{"d2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }

    //@Test
    public void testYosemiteDigitalContractJ() throws InterruptedException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteDigitalContractJ yxj = new YosemiteDigitalContractJ(apiClient);

        // 0. remove digital contract first
        PushedTransaction pushedTransaction;
        try {
            pushedTransaction = yxj.removeDigitalContract("servprovider", 11, new String[]{"servprovider@active"}).join();
            logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        } catch (Exception ignored) {
        }

        // 1. create digital contract
        List<String> signers = Arrays.asList("user1", "user2");
        // prepare expiration time based on UTC time-zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR, 48);
        Date expirationTime = calendar.getTime();

        pushedTransaction = yxj.createDigitalContract("servprovider", 11, "test1234", "",
                signers, expirationTime, 0, EnumSet.noneOf(KYCStatusType.class), (short) 0, new String[]{"servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        // 2. add additional signers if needed
        List<String> newSigners = Collections.singletonList("user3");
        pushedTransaction = yxj.addSigners("servprovider", 11, newSigners, new String[]{"servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        Thread.sleep(1000);

        // 3. sign contract by signers
        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user2", "", new String[]{"user2@active", "servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user3", "I am user3", new String[]{"user3@active", "servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        pushedTransaction = yxj.updateAdditionalDocumentHash("servprovider", 11, "added after signing", new String[]{"servprovider@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        TableRow tableRow = yxj.getCreatedDigitalContract("servprovider", 11).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            logger.debug(row.toString());
        }

        TableRow signerInfoTable = yxj.getSignerInfo("user3", "servprovider", 11).join();
        logger.debug(String.valueOf(signerInfoTable.getMore()));
        for (Map<String, ?> row : signerInfoTable.getRows()) {
            // There must be only one row.
            logger.debug(row.toString());
            logger.debug((String) row.get("signerinfo"));
        }
    }

    private class TestEventCallback implements EventNotificationCallback<TxIrreversibilityResponse> {

        @Override
        public void eventNotified(TxIrreversibilityResponse response, Map<String, Object> responseJsonMap) {
            logger.debug(responseJsonMap.toString());
        }

        @Override
        public void errorOccurred(Throwable error) {
        }
    }

    //@Test
    public void testYosemiteEventNotificationWithDigitalContract() throws InterruptedException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        YosemiteEventNotificationClient yosemiteEventNotificationClient =
                YosemiteEventNotificationClientFactory.createYosemiteEventNotificationClient("ws://127.0.0.1:8888");
        yosemiteEventNotificationClient.subscribe();

        YosemiteDigitalContractJ yxj = new YosemiteDigitalContractJ(apiClient);

        // 0. remove digital contract first
        PushedTransaction pushedTransaction;
        try {
            pushedTransaction = yxj.removeDigitalContract("servprovider", 11, new String[]{"servprovider@active"}).join();
            logger.debug("Pushed Transaction Id: " + pushedTransaction.getTransactionId());
            yosemiteEventNotificationClient.checkTransactionIrreversibility(pushedTransaction.getTransactionId(), new TestEventCallback());
        } catch (Exception ignored) {
        }

        // 1. create digital contract
        List<String> signers = Arrays.asList("user1", "user2");
        // prepare expiration time based on UTC time-zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR, 48);
        Date expirationTime = calendar.getTime();

        pushedTransaction = yxj.createDigitalContract("servprovider", 11, "test1234", "",
                signers, expirationTime, 0, EnumSet.noneOf(KYCStatusType.class), (short) 0, new String[]{"servprovider@active"}).join();
        logger.debug("Pushed Transaction Id: " + pushedTransaction.getTransactionId());
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
        yosemiteEventNotificationClient.checkTransactionIrreversibility(pushedTransaction.getTransactionId(), new TestEventCallback());

        // 2. sign contract by signers
        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user2", "", null).join();
        logger.debug("Pushed Transaction Id: " + pushedTransaction.getTransactionId());
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
        yosemiteEventNotificationClient.checkTransactionIrreversibility(pushedTransaction.getTransactionId(), new TestEventCallback());

        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user1", "I am user1", null).join();
        logger.debug("Pushed Transaction Id: " + pushedTransaction.getTransactionId());
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
        yosemiteEventNotificationClient.checkTransactionIrreversibility(pushedTransaction.getTransactionId(), new TestEventCallback());

        Thread.sleep(5000);
        yosemiteEventNotificationClient.unsubscribe();
    }
}
