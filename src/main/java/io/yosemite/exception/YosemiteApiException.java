package io.yosemite.exception;

import javax.annotation.Nullable;

public class YosemiteApiException extends RuntimeException {

    @Nullable private YosemiteApiError error;
    @Nullable private ErrorCode errorCode;
    @Nullable private String transactionId;
    private volatile String message;

    public YosemiteApiException(String error) {
        super(error, null);
    }

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

    @Override
    public String getMessage() {
        if (transactionId == null) return super.getMessage();
        String message = this.message;
        if (message == null) {
            message = "\ntransaction id = " + transactionId + "\nmessage = " + super.getMessage();
            this.message = message;
        }
        return message;
    }
}
