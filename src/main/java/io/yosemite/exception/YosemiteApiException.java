package io.yosemite.exception;

public class YosemiteApiException extends RuntimeException {

    private YosemiteApiError error;

    private ErrorCode yosemiteErrorCode;

    public YosemiteApiException(ErrorCode yosemiteErrorCode) {
        this.yosemiteErrorCode = yosemiteErrorCode;
    }

    public YosemiteApiException(YosemiteApiError apiError) {
        this.error = apiError;
    }

    public YosemiteApiException(String message, ErrorCode yosemiteErrorCode) {
        super(message);
        this.yosemiteErrorCode = yosemiteErrorCode;
    }

    public YosemiteApiException(Throwable cause, ErrorCode yosemiteErrorCode) {
        super(cause);
        this.yosemiteErrorCode = yosemiteErrorCode;
    }

    public YosemiteApiException(Throwable cause) {
        super(cause);
    }

    public YosemiteApiException(String message, Throwable cause, ErrorCode yosemiteErrorCode) {
        super(message, cause);
        this.yosemiteErrorCode = yosemiteErrorCode;
    }

    public ErrorCode getYosemiteErrorCode() {
        return yosemiteErrorCode;
    }

    public void setYosemiteErrorCode(ErrorCode yosemiteErrorCode) {
        this.yosemiteErrorCode = yosemiteErrorCode;
    }

}
