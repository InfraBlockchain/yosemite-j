package io.yosemite.services.event;

public class YosemiteEventNotificationClientFactory {

    private YosemiteEventNotificationClientFactory() { }

    /**
     * Create a new YosemiteEventNotificationClient instance with the base ws address only.
     * @param baseWsUrl WebSocket URL to the Yosemite node for event notification
     * @return the new YosemiteEventNotificationClient instance
     */
    public static YosemiteEventNotificationClient createYosemiteEventNotificationClient(String baseWsUrl) {
        return createYosemiteEventNotificationClient(baseWsUrl, null);
    }

    /**
     * Create a new YosemiteEventNotificationClient instance with the base ws address and the wallet daemon HTTP address.
     * @param baseUrl WebSocket URL to the Yosemite node for event notification
     * @param walletBaseUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @return the new YosemiteEventNotificationClient instance
     */
    public static YosemiteEventNotificationClient createYosemiteEventNotificationClient(String baseUrl, String walletBaseUrl) {
        return new YosemiteEventNotificationClient(baseUrl, walletBaseUrl);
    }

}
