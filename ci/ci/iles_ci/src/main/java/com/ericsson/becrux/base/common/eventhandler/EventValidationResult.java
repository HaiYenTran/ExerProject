package com.ericsson.becrux.base.common.eventhandler;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides feedback from the {@link EventHandlingStrategy}
 * for the {@link EventHandler} regarding the @{@link Event}.
 * <p/>
 * Event Handler will treat the value in one of three ways depending on the flags set:
 * successful : will handle
 * failure - will not handle and will report failure - used when the value is corrupted, can't be handled due to system
 *      state or an exception occurred during validation process
 * <p/>
 * Adding errors to the result will mark it as a failure. Comments do not affect the result.
 */
public class EventValidationResult {

    //Optional error field
    private List<String> comments = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public EventValidationResult() {

    }


    public EventValidationResult(String error) {
        if (error != null)
            addError(error);
    }

    public boolean isSuccessful() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        if (errors == null)
            throw new NullPointerException("List of errors cannot be null");
        if (errors.stream().anyMatch(e -> e == null))
            throw new NullPointerException("One of errors is null");
        this.errors = errors;
    }

    public void addError(String error) {
        if (error == null)
            throw new NullPointerException("Error can not be null");
        this.errors.add(error);
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        if (comments == null)
            throw new NullPointerException("List of comments cannot be null");
        if (comments.stream().anyMatch(e -> e == null))
            throw new NullPointerException("One of comments is null");
        this.comments = comments;
    }

    public void addComment(String comment) {
        if (comment == null)
            throw new NullPointerException("Comment can not be null");
        this.comments.add(comment);
    }
}
