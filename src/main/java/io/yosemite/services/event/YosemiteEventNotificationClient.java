package io.yosemite.services.event;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.yosemite.data.remote.event.ErrorResponse;
import io.yosemite.data.remote.event.TxIrreversibilityRequest;
import io.yosemite.data.remote.event.TxIrreversibilityResponse;
import io.yosemite.services.ApiServiceExecutor;
import io.yosemite.services.YosemiteWalletApiService;
import io.yosemite.util.Utils;
import okhttp3.*;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Eugene Chung
 */
public class YosemiteEventNotificationClient extends WebSocketListener {
    private final static Logger logger = LoggerFactory.getLogger(YosemiteEventNotificationClient.class);

    private long requestIdSequence;
    private final String baseWsUrl;
    private final ApiServiceExecutor<YosemiteWalletApiService> yxWalletApiService;
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private WebSocket webSocket;
    private final ConcurrentHashMap<String, EventNotificationCallback<?>> requestIdToCallbackMap = new ConcurrentHashMap<>();

    YosemiteEventNotificationClient(String baseWsUrl, String walletHttpUrl) {
        this.baseWsUrl = baseWsUrl;
        if (walletHttpUrl != null) {
            yxWalletApiService = ApiServiceExecutor.create(YosemiteWalletApiService.class, walletHttpUrl);
        } else {
            yxWalletApiService = null;
        }
        okHttpClient = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
        gson = Utils.createYosemiteJGsonBuilder().create();
    }

    private void checkConnected() {
        if (webSocket == null) throw new IllegalStateException("client is unsubscribed");
    }

    public void subscribe() {
        if (webSocket != null) throw new IllegalStateException("client is already subscribed");

        Request request = new Request.Builder().url(baseWsUrl + "/v1/event_notification").build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, new InternalWebSocketListener());
        //webSocket.send();
        setWebSocket(webSocket);
    }

    public void unsubscribe() {
        checkConnected();
        webSocket.close(1000, null);
        okHttpClient.dispatcher().executorService().shutdown();
    }

    public String checkTransactionIrreversibility(String transactionId, EventNotificationCallback<TxIrreversibilityResponse> callback) {
        return checkTransactionIrreversibility(transactionId, null, callback);
    }

    public String checkTransactionIrreversibility(String transactionId, Long blockNumberHint, EventNotificationCallback<TxIrreversibilityResponse> callback) {
        checkConnected();

        String requestId = generateRequestId();
        TxIrreversibilityRequest request = new TxIrreversibilityRequest();
        request.setRequestId(requestId);
        request.setName(EventNames.TX_IRREVERSIBILITY.getName());
        request.setTransactionId(transactionId);
        request.setBlockNumberHint(blockNumberHint);

        requestIdToCallbackMap.put(requestId, callback);
        boolean result = webSocket.send(gson.toJson(request));
        if (!result) {
            requestIdToCallbackMap.remove(requestId);
            callback.errorOccurred(new WebSocketException("WebSocket send failed"));
            return null;
        }

        return requestId;
    }

    private String generateRequestId() {
        return String.valueOf(++requestIdSequence);
    }

    void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    private class InternalWebSocketListener extends WebSocketListener {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Map<String, Object> resultMap = gson.fromJson(text, Map.class);
            String name = (String) resultMap.get("name");
            if (EventNames.TX_IRREVERSIBILITY.getName().equals(name)) {
                JsonElement jsonElement = gson.toJsonTree(resultMap);
                TxIrreversibilityResponse response = gson.fromJson(jsonElement, TxIrreversibilityResponse.class);

                EventNotificationCallback<TxIrreversibilityResponse> callback =
                        (EventNotificationCallback<TxIrreversibilityResponse>) requestIdToCallbackMap.remove(response.getRequestId());
                if (callback != null) {
                    callback.eventNotified(response, resultMap);
                }
            } else if (EventNames.ERROR.getName().equals(name)) {
                JsonElement jsonElement = gson.toJsonTree(resultMap);
                ErrorResponse response = gson.fromJson(jsonElement, ErrorResponse.class);

                EventNotificationCallback<TxIrreversibilityResponse> callback =
                        (EventNotificationCallback<TxIrreversibilityResponse>) requestIdToCallbackMap.remove(response.getRequestId());
                if (callback != null) {
                    callback.errorOccurred(new EventNotificationException(response.getCode(), response.getMessage()));
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            throw new IllegalStateException("Binary message is not used");
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            setWebSocket(null);
            for (EventNotificationCallback<?> callback : requestIdToCallbackMap.values()) {
                callback.errorOccurred(new WebSocketException("WebSocket closed by remote peer"));
            }
            requestIdToCallbackMap.clear();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            setWebSocket(null);
            for (EventNotificationCallback<?> callback : requestIdToCallbackMap.values()) {
                callback.errorOccurred(new WebSocketException(t));
            }
            requestIdToCallbackMap.clear();
        }
    }
}
