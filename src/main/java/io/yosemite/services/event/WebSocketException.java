package io.yosemite.services.event;

/**
 * @author Eugene Chung
 */
public class WebSocketException extends EventNotificationException {
    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(Throwable t) {
        super(t);
    }
}
