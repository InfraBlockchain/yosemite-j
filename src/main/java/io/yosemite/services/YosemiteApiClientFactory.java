package io.yosemite.services;

public class YosemiteApiClientFactory {
    private YosemiteApiClientFactory() { }

    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl) {
        return new YosemiteApiRestClientImpl(baseUrl);
    }

    public static YosemiteApiRestClient createYosemiteApiClient(String baseUrl, String walletBaseUrl, String historyBaseUrl) {
        return new YosemiteApiRestClientImpl(baseUrl, walletBaseUrl, historyBaseUrl);
    }
}
