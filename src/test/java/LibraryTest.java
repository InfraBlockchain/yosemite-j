import io.yosemite.data.remote.model.chain.Info;
import io.yosemite.data.remote.model.response.history.action.Action;
import io.yosemite.data.remote.model.response.history.action.Actions;
import io.yosemite.services.*;
import io.yosemite.util.Consts;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.yosemite.data.remote.model.chain.PushedTransaction;
import io.yosemite.util.Utils;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class LibraryTest {

    final static Logger logger = LoggerFactory.getLogger(LibraryTest.class);

    //@Test
    public void testGetInfo() throws IOException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Info result = apiClient.getInfo().execute();
        System.out.println(result.getBrief());
    }

    //@Test
    public void testGetActions() throws IOException {

        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
                "http://127.0.0.1:8888", "http://127.0.0.1:8900", "http://127.0.0.1:8888");
        Actions result = apiClient.getActions(Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT, -1, -20).execute();
        System.out.println("LastIrreversibleBlock : " + result.getLastIrreversibleBlock());
        for (Action action : result.getActions()) {
            System.out.println(action.getAccountActionSeq() + " " + action.getBlockNum());
        }
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

    /*
    @Test
    public void testYxjNativeToken() {

        YxApiRestClient apiClient = new YxApiRestClientImpl("http://testnet.yosemitelabs.org:8888", "http://127.0.0.1:8900");

        Yxj yxj = new Yxj(apiClient);

        PushedTransaction pushedTransaction = yxj.issueNativeToken("producer.a", "10000.0000 DKRW", "sysdepo", "", new String[]{"sysdepo@active"}).join();

        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));

        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }
    */

    /*
    @Test
    public void testSynchronousRestApiClientTest() throws Exception {

        YxApiRestClient apiClient = new YxApiRestClientImpl("http://127.0.0.1:8888");

        // Push Contract Tx by packing the data

        String contract = "eosio.token"; // sometimes called `code`
        String action = "transfer";
        String args = "{\"from\":\"user\",\"to\":\"tester\",\"quantity\":\"1.0000 SYS\",\"memo\":\"wow\"}";
        String[] permissions = new String[]{"user@active"};

        AbiJsonToBinReq abiJsonToBinReq = new AbiJsonToBinReq(contract, action, args);

        Info info = apiClient.getInfo().execute();

        logger.debug(info.getBrief());

        AbiJsonToBinRes abiJsonToBinRes = apiClient.abiJsonToBin(abiJsonToBinReq).execute();

        logger.debug(abiJsonToBinRes.getBinargs());

        Action actionReq = new Action(contract, action);
        actionReq.setAuthorization(permissions);
        actionReq.setData(abiJsonToBinRes.getBinargs());

        SignedTransaction txReq = new SignedTransaction();
        txReq.addAction(actionReq);
        txReq.setReferenceBlock(info.getHeadBlockId());
        txReq.setExpiration(info.getTimeAfterHeadBlockTime(300000));

        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("EOS8ePyQrK7XZKUKSbhKuGVCLc4XfFp4N3sf3uCZSsEDTzZXLfNVj");

        GetRequiredKeysReq getRequiredKeysReq = new GetRequiredKeysReq(txReq, pubKeys);

        GetRequiredKeysRes getRequiredKeysRes = apiClient.getRequiredKeys(getRequiredKeysReq).execute();

        List<String> keys = getRequiredKeysRes.getRequiredKeys();

        for (String key : keys) {
            logger.debug("Pub key: " + key);
        }

        SignedTransaction signedTx = apiClient.signTransaction(txReq, keys, info.getChainId()).execute();

        logger.debug("\nSigned Transaction:\n" + Utils.prettyPrintJson(signedTx));

        PackedTransaction packedTx = new PackedTransaction(signedTx);

        logger.debug("\nPacked Transaction:\n" + Utils.prettyPrintJson(packedTx));

        PushedTransaction pushedTransaction = apiClient.pushTransaction(packedTx).execute();

        logger.debug("\nPushed Transaction:\n" + Utils.prettyPrintJson(pushedTransaction));

        assertTrue("Success", !pushedTransaction.getTransactionId().isEmpty());
    }
    */
}
