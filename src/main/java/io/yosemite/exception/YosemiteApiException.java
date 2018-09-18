package io.yosemite.exception;

public class YosemiteApiException extends RuntimeException {

    private YosemiteApiError error;

    public YosemiteApiException(YosemiteApiError apiError) {
        super(apiError.getDetailedMessage());
        this.error = apiError;
    }

    public YosemiteApiException(YosemiteApiError apiError, Throwable cause) {
        super(apiError.getDetailedMessage(), cause);
        this.error = apiError;
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
}
