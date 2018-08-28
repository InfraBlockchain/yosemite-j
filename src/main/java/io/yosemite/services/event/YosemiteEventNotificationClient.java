package io.yosemite.services.event;

import com.google.gson.Gson;
import io.yosemite.data.remote.event.TxIrreversibilityParameters;
import io.yosemite.data.remote.event.TxIrreversibilityRequest;
import io.yosemite.services.ApiServiceExecutor;
import io.yosemite.services.YosemiteWalletApiService;
import io.yosemite.util.Utils;
import okhttp3.*;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Eugene Chung
 */
public class YosemiteEventNotificationClient extends WebSocketListener {
    private final static Logger logger = LoggerFactory.getLogger(YosemiteEventNotificationClient.class);

    private final String baseWsUrl;
    private final ApiServiceExecutor<YosemiteWalletApiService> yxWalletApiService;
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private WebSocket webSocket;

    public YosemiteEventNotificationClient(String baseWsUrl, String walletHttpUrl) {
        this.baseWsUrl = baseWsUrl;
        if (walletHttpUrl != null) {
            yxWalletApiService = ApiServiceExecutor.create(YosemiteWalletApiService.class, walletHttpUrl);
        } else {
            yxWalletApiService = null;
        }
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        gson = Utils.createYosemiteJGsonBuilder().create();
    }

    private void checkConnected() {
        if (webSocket == null) throw new IllegalStateException("event notification is unsubscribed");
    }

    public void subscribe() {
        if (webSocket != null) throw new IllegalStateException("event notification is already subscribed");

        Request request = new Request.Builder().url(baseWsUrl + "/v1/event_notification").build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, new EventNotificationListener());
        //webSocket.send();
        setWebSocket(webSocket);
    }

    public void unsubscribe() {
        checkConnected();
        webSocket.close(1000, null);
        okHttpClient.dispatcher().executorService().shutdown();
    }

    public void registerTransactionId(String transactionId) {
        registerTransactionId(transactionId, null);
    }

    public void registerTransactionId(String transactionId, Long blockNumberHint) {
        checkConnected();
        TxIrreversibilityParameters parameters = new TxIrreversibilityParameters();
        parameters.setTransactionId(transactionId);
        parameters.setBlockNumberHint(blockNumberHint);
        TxIrreversibilityRequest request = new TxIrreversibilityRequest();
        request.setParameters(parameters);

        webSocket.send(gson.toJson(request));
    }

    void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    private class EventNotificationListener extends WebSocketListener {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            logger.debug("MESSAGE: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            logger.debug("MESSAGE: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            setWebSocket(null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            setWebSocket(null);
        }
    }
}
