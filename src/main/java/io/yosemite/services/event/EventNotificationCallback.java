package io.yosemite.services.event;

import java.util.Map;

public interface EventNotificationCallback<T> {
    void eventNotified(T response, Map<String, Object> responseJsonMap);
    void errorOccurred(Throwable error);
}
