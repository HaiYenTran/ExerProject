package com.ericsson.becrux.iles.exceptions;

import java.util.List;

public class IlesDirectoryException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -339131085906287485L;

    public IlesDirectoryException() {
        super();
    }

    public IlesDirectoryException(String message) {
        super(message);
    }

    public IlesDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public IlesDirectoryException(Throwable cause) {
        super(cause);
    }

    public IlesDirectoryException(List<String> messages) {
        super(buildErrorMessage(messages));
    }

    public IlesDirectoryException(List<String> messages, Throwable cause) {
        super(buildErrorMessage(messages), cause);
    }

    private static String buildErrorMessage(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        for (String m : messages) {
            if (builder.length() != 0)
                builder.append(";").append(System.lineSeparator());
            builder.append(m);
        }
        return builder.toString();
    }

    public String getHtmlMessage() {
        return super.getMessage().replaceAll(System.lineSeparator(), "<br/>");
    }

}
