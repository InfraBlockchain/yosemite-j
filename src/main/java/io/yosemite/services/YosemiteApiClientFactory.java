package io.yosemite.services;

import dagger.Component;
import io.yosemite.LibraryModule;
import io.yosemite.util.Consts;

import javax.inject.Singleton;

public class YosemiteApiClientFactory {

    private YosemiteApiClientFactory() { }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL only.
     * @param baseUrl HTTP URL to the Yosemite node
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl) {
        return createYosemiteApiClient(baseUrl, Consts.TX_EXPIRATION_IN_MILLIS);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL and transaction expiration time.
     * @param baseUrl HTTP URL to the Yosemite node
     * @param txExpirationInMillis transaction expiration time in milliseconds
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, int txExpirationInMillis) {
        return createYosemiteApiClient(baseUrl, baseUrl, baseUrl, txExpirationInMillis);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL and the wallet daemon URL.
     * @param baseUrl HTTP URL to the Yosemite node
     * @param walletBaseUrl HTTP URL to the Yosemite wallet daemon(kyxd)
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl) {
        return createYosemiteApiClient(baseUrl, walletBaseUrl, Consts.TX_EXPIRATION_IN_MILLIS);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL and transaction expiration time.
     * @param baseUrl HTTP URL to the Yosemite node
     * @param walletBaseUrl HTTP URL to the Yosemite wallet daemon(kyxd)
     * @param txExpirationInMillis transaction expiration time in milliseconds
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl, int txExpirationInMillis) {
        return createYosemiteApiClient(baseUrl, walletBaseUrl, baseUrl, txExpirationInMillis);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL and the history server URL.
     * @param baseUrl HTTP URL to the Yosemite node
     * @param walletBaseUrl HTTP URL to the Yosemite wallet daemon(kyxd)
     * @param historyBaseUrl HTTP URL to the Yosemite node which provides transaction history log
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl, String historyBaseUrl) {
        return createYosemiteApiClient(baseUrl, walletBaseUrl, historyBaseUrl, Consts.TX_EXPIRATION_IN_MILLIS);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL,
     * the history server URL and transaction expiration time.
     * @param baseUrl HTTP URL to the Yosemite node
     * @param walletBaseUrl HTTP URL to the Yosemite wallet daemon(kyxd)
     * @param historyBaseUrl HTTP URL to the Yosemite node which provides transaction history log
     * @param txExpirationInMillis transaction expiration time in milliseconds
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl,
                                                                String historyBaseUrl, int txExpirationInMillis) {



        return new YosemiteApiRestClientImpl(baseUrl, walletBaseUrl, historyBaseUrl, txExpirationInMillis);
    }
}
