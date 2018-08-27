import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.yosemite.crypto.digest.Sha256;
import io.yosemite.crypto.ec.EcDsa;
import io.yosemite.crypto.ec.EcSignature;
import io.yosemite.data.remote.chain.Block;
import io.yosemite.data.remote.chain.Info;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.chain.account.Account;
import io.yosemite.data.remote.history.action.Actions;
import io.yosemite.data.remote.history.action.OrderedActionResult;
import io.yosemite.data.remote.history.transaction.Transaction;
import io.yosemite.data.util.GsonYosemiteTypeAdapterFactory;
import io.yosemite.services.YosemiteApiClientFactory;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.services.yxcontracts.YosemiteSystemJ;
import io.yosemite.services.yxcontracts.KYCStatusType;
import io.yosemite.services.yxcontracts.YosemiteDigitalContractJ;
import io.yosemite.services.yxcontracts.YosemiteNativeTokenJ;
import io.yosemite.services.yxcontracts.YosemiteTokenJ;
import io.yosemite.util.Consts;
import io.yosemite.util.StringUtils;
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

    private Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonYosemiteTypeAdapterFactory())
            .excludeFieldsWithoutExposeAnnotation().create();

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
    public void testGetActions() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Actions result = apiClient.getActions(Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT, -1, -20).execute();
        System.out.println("LastIrreversibleBlock : " + result.getLastIrreversibleBlock());
        for (OrderedActionResult action : result.getActions()) {
            System.out.println(action.getAccountActionSeq() + " " + action.getBlockNum());
        }
    }

    //@Test
    public void testGetTransaction() throws IOException {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Transaction result = apiClient.getTransaction("a4f2bfe30205cf8805aa17014759152414bc6db6879b9de465fefe91cd118db5").execute();
        System.out.println("LastIrreversibleBlock : " + result.getLastIrreversibleBlock());
        System.out.println("Block : " + result.getBlockNum());
        System.out.println(Utils.prettyPrintJson(result));
    }

    //@Test
    public void testYosemiteNativeTokenJ() {
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");

        YosemiteJ yxj = new YosemiteNativeTokenJ(apiClient);

        String contract = "yx.ntoken";
        String action = "transfer";
        String data = "{\"from\":\"user\",\"to\":\"tester\",\"amount\":\"2.0000 DKRW\",\"memo\":\"test\"}";
        String[] permissions = new String[]{"user@active"};

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
        pushedTransaction = yxj.setTokenKYCRule("XYZ", 4, "d2", YosemiteTokenJ.KYCRuleType.KYC_RULE_TRANSFER_RECEIVE, kycStatusPhoneAuth, new String[]{"d2@active"}).join();
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
                signers, expirationTime, (short) 0, new String[]{"servprovider@active"}).join();
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
        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user2", "", new String[]{"user2@active"}).join();
        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));
        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());

        pushedTransaction = yxj.signDigitalDocument("servprovider", 11, "user3", "I am user3", new String[]{"user3@active"}).join();
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
            logger.debug(StringUtils.convertHexToString((String) row.get("signerinfo")));
        }
    }

}
