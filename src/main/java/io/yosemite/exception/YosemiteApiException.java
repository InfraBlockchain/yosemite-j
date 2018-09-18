package io.yosemite.exception;

public class YosemiteApiException extends RuntimeException {

    private YosemiteApiError error;
    private ErrorCode errorCode;

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
}
