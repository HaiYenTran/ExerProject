package com.ericsson.becrux.base.common.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Becrux General exception.
 */
public class BecruxException extends Exception {

    private List<String> messages = new ArrayList<>();
    private String type = "Becrux General Exception"; // default type
    private String description;

    // TODO: add more constructor

    /**
     * Constructor.
     * @param type the type of BecruxException
     * @param messages list of error message
     * @param description
     */
    public BecruxException(String type, List<String> messages, String description) {
        this.type = type;
        this.description = description;
        add(messages);
    }

    /**
     * Constructor.
     * @param type
     * @param message
     * @param description
     */
    public BecruxException(String type, String message, String description) {
        super(message);
        this.description = description;
    }

    /**
     * Constructor.
     * @param e
     */
    public BecruxException(String type, Throwable e) {
        super(e);
        messages.add(e.getLocalizedMessage());
    }

    public BecruxException() {
        super();
    }

    /**
     * Add more error message.
     * @param errorMessage
     */
    public void add(String errorMessage) {
        messages.add(errorMessage);
    }

    /**
     * Add more error messages.
     * @param errorMessage
     */
    public void add(List<String> errorMessage) {
        messages.addAll(errorMessage);
    }

    /**
     * Add more error Exception.
     * @param exception
     */
    public void add(Throwable exception) {
        messages.add(exception.getLocalizedMessage());
    }

    /**
     * Get the message with HTML format.
     * @return
     */
    public String getHtmlMessage() {
        return super.getMessage().replaceAll(System.lineSeparator(), "<br/>");
    }

    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return buildErrorMessage(this.messages);
    }

    private String buildErrorMessage(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append(":");
        for (String m : messages) {
            if (builder.length() != 0)
                builder.append(";").append(System.lineSeparator());
            builder.append(m);
        }
        return builder.toString();
    }

    /**
     * Get list of error message.
     * @return
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Get the Exception type.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Get the Exception description.
     * @return
     */
    public String getDescription() {
        return description;
    }

}
