package com.ericsson.becrux.base.common.exceptions;

import java.util.List;

public class BecruxDirectoryException extends BecruxException {

    private static final long serialVersionUID = -339131085906287485L;

    private static final String TYPE = "Becrux Directory Exception";
    private static final String DESCRIPTION = "";

    public BecruxDirectoryException() {
        super();
    }

    public BecruxDirectoryException(String message) {
        super(TYPE, message, DESCRIPTION);
    }

    public BecruxDirectoryException(List<String> messages) {
        super(TYPE, messages, DESCRIPTION);
    }

    public BecruxDirectoryException(Throwable e) {
        super(TYPE, e);
    }
}
