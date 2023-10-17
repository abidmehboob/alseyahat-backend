package com.alseyahat.app.feature.notification.service;

import java.util.Locale;

@FunctionalInterface
public interface NotificationService<T> {

    void prepareAndSendMessage(final T data, final String notificationType, final Locale locale, final String template);

}