package com.ericsson.becrux.iles.eiffel.eventrepository;

// TODO: review if it can be on CORE ?
public class EventRepositoryException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 2915629828258904677L;

    private final static String defaultMessage = "Event repository communicator exception";

    public EventRepositoryException() {
        super(defaultMessage);
    }

    public EventRepositoryException(String message) {
        super(message);
    }

    public EventRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventRepositoryException(Throwable cause) {
        super(defaultMessage, cause);
    }
}
