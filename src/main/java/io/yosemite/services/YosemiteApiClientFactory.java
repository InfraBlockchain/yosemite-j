package io.yosemite.services;

import io.yosemite.Consts;
import io.yosemite.util.StringUtils;

public class YosemiteApiClientFactory {

    private YosemiteApiClientFactory() { }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL and the wallet daemon URL.
     * @param nodeUrl HTTP URL to the Yosemite node
     * @param keyosUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String nodeUrl, String keyosUrl) {
        return createYosemiteApiClient(nodeUrl, keyosUrl, Consts.TX_EXPIRATION_IN_MILLIS);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL and transaction expiration time.
     * If the transaction is not irreversible in the expiration time, it's expired or dropped.
     * @param nodeUrl HTTP URL to the Yosemite node
     * @param keyosUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @param txExpirationInMillis transaction expiration time in milliseconds
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String nodeUrl, String keyosUrl, int txExpirationInMillis) {
        return createYosemiteApiClient(nodeUrl, keyosUrl, null, txExpirationInMillis, null);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL and the history server URL.
     * @param nodeUrl HTTP URL to the Yosemite node
     * @param keyosUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @param explorerUrl HTTP URL to the Yosemite chain explorer server which provides transaction history log
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String nodeUrl, String keyosUrl, String explorerUrl) {
        return createYosemiteApiClient(nodeUrl, keyosUrl, explorerUrl, null);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL,
     * the history server URL and transaction expiration time.
     * If the transaction is not irreversible in the expiration time, it's expired or dropped.
     * @param nodeUrl HTTP URL to the Yosemite node
     * @param keyosUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @param explorerUrl HTTP URL to the Yosemite chain explorer server which provides transaction history log
     * @param transactionVoteTarget The account name to vote for PoT
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String nodeUrl, String keyosUrl,
                                                                String explorerUrl, String transactionVoteTarget) {
        return createYosemiteApiClient(nodeUrl, keyosUrl, explorerUrl, Consts.TX_EXPIRATION_IN_MILLIS, transactionVoteTarget);
    }

    /**
     * Create a new YosemiteApiRestClient instance with the base URL, the wallet daemon URL,
     * the history server URL and transaction expiration time.
     * If the transaction is not irreversible in the expiration time, it's expired or dropped.
     * @param nodeUrl HTTP URL to the Yosemite node
     * @param keyosUrl HTTP URL to the Yosemite wallet daemon(keyos)
     * @param explorerUrl HTTP URL to the Yosemite chain explorer server which provides transaction history log
     * @param txExpirationInMillis transaction expiration time in milliseconds
     * @param transactionVoteTarget The account name which is the target of transaction vote
     * @return the new YosemiteApiRestClient instance
     */
    public static YosemiteApiRestClient createYosemiteApiClient(String nodeUrl, String keyosUrl,
                                                                String explorerUrl, int txExpirationInMillis,
                                                                String transactionVoteTarget) {
        if (StringUtils.isEmpty(nodeUrl)) throw new IllegalArgumentException("empty nodeUrl");
        if (StringUtils.isEmpty(keyosUrl)) throw new IllegalArgumentException("empty keyosUrl");
        return new YosemiteApiRestClientImpl(nodeUrl, keyosUrl, explorerUrl, txExpirationInMillis, transactionVoteTarget);
    }
}
