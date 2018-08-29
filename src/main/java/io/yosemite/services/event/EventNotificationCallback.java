package io.yosemite.services.event;

import java.util.Map;

public interface EventNotificationCallback<T> {
    /**
     * Called when the event is notified from the Yosemite node.
     * @param response T instance made from the json response by Gson
     * @param responseJsonMap Map instance from the json response by Gson
     */
    void eventNotified(T response, Map<String, Object> responseJsonMap);

    /**
     * Called when the error is occurred.
     * @param error Throwable instance; usually the instance of EventNotificationException is passed
     */
    void errorOccurred(Throwable error);
}
