package com.ecom.catalog.admin.domain.exceptions;

public class InternalErrorException extends NoStacktraceException {
    protected InternalErrorException(final String aMessage, final Throwable t) {
        super(aMessage, t);
    }

    protected InternalErrorException(final String aMessage) {
        super(aMessage);
    }

    public static InternalErrorException with(final String aMessage, final Throwable t) {
        return new InternalErrorException(aMessage, t);
    }

    public static InternalErrorException with(final String aMessage) {
        return new InternalErrorException(aMessage);
    }

}
