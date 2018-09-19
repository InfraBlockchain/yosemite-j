package io.yosemite.exception;

public class YosemiteApiException extends RuntimeException {

    private YosemiteApiError error;
    private ErrorCode errorCode;
    private String transactionId;

    public YosemiteApiException(YosemiteApiError apiError) {
        this(apiError, null);
    }

    public YosemiteApiException(YosemiteApiError apiError, Throwable cause) {
        super(apiError.getDetailedMessage(), cause);
        this.error = apiError;
        this.errorCode = YosemiteApiErrorCode.get(apiError.getCode());
    }

    public YosemiteApiException(Throwable cause) {
        super(cause);
    }

    public YosemiteApiError getError() {
        return error;
    }

    public void setError(YosemiteApiError error) {
        this.error = error;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Return the id of the failed transaction.
     * You can check the detailed error message from the block explorer.
     * e.g. http://testnet-explorer-api.yosemitelabs.org/transaction/9ff43da561794d0010792c40162b2dc20aa2aec50dd355739240ffb3f6a5507d
     *      Note that 9ff43da561794d0010792c40162b2dc20aa2aec50dd355739240ffb3f6a5507d is the example of the transaction id.
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }
}
