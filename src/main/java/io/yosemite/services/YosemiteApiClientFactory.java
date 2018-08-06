package io.yosemite.services;

public class YosemiteApiClientFactory {
    private YosemiteApiClientFactory() { }

    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl) {
        return createYosemiteApiClient(baseUrl, baseUrl, baseUrl);
    }

    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl) {
        return createYosemiteApiClient(baseUrl, walletBaseUrl, baseUrl);
    }

    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl, String historyBaseUrl) {
        return new YosemiteApiRestClientImpl(baseUrl, walletBaseUrl, historyBaseUrl);
    }
}
