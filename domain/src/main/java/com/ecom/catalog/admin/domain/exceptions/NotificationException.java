package com.ecom.catalog.admin.domain.exceptions;


import com.ecom.catalog.admin.domain.validation.handler.Notification;

public class NotificationException extends DomainException {

    public NotificationException(final String aMessage, final Notification aNotification) {
        super(aMessage, aNotification.getErrors());
    }
}
