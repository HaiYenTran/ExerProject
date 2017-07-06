package com.ericsson.becrux.base.common.eiffel.exceptions;

import com.ericsson.becrux.base.common.exceptions.BecruxException;

import java.util.List;

/**
 * TODO: why we need ?
 */
public class EiffelException extends BecruxException {
    /**
     *
     */
    private static final long serialVersionUID = 6106190991137386565L;

    private static final String TYPE = "Eiffel Exception";
    private static final String DESCRIPTION = "";

    public EiffelException() {
        super();
    }

    public EiffelException(String message) {
        super(TYPE, message, DESCRIPTION);
    }

    public EiffelException(List<String> messages) {
        super(TYPE, messages, DESCRIPTION);
    }

    public EiffelException(Throwable e) {
        super(TYPE, e);
    }
}
