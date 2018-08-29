package io.yosemite.services.event;

/**
 * @author Eugene Chung
 */
public class EventNotificationException extends Exception {
    private int code;

    public EventNotificationException(String message) {
        super(message);
    }

    public EventNotificationException(int code, String message) {
        super(message);
        this.code = code;
    }

    public EventNotificationException(Throwable t) {
        super(t);
    }

    public int getCode() {
        return code;
    }
}
