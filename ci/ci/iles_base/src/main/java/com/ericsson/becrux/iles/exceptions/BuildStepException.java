package com.ericsson.becrux.iles.exceptions;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

/**
 * General Exception for Jenkins BuildStep.
 *
 * @author DungB
 */
public class BuildStepException extends Exception {

    /**
     * String pattern for Unsuccessful process error message.
     * TODO: review the message
     */
    private static String UNSUCCESSFUL_EXCEPTION_MESSAGE = "{0} failed to process with error : {1}";

    /**
     * Constructor.
     * @param buildStepName name of the BuildStep
     * @param e the exception
     */
    public BuildStepException(@Nonnull String buildStepName,@Nonnull Exception e) {
        super(MessageFormat.format(UNSUCCESSFUL_EXCEPTION_MESSAGE, buildStepName, e));
    }
}
