package com.ericsson.becrux.base.common.eventhandler;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import static com.ericsson.becrux.base.common.eventhandler.EventHandlingResult.SimpleResult.*;

/**
 * Class used by the {@link EventHandlingStrategy}
 * to pass the results of handling an {@link Event}.
 */
public class EventHandlingResult {

    /**
     * Creates a new {@link EventHandlingResult} to indicate a successful handling of the
     * {@link Event} by an
     * {@link EventHandlingStrategy}
     *
     * @return a result that indicates a success
     */
    public static EventHandlingResult createSuccessfulResult() {
        return new EventHandlingResult("Event handled successfully.", null, true, false, 0, 0, false);
    }

    /**
     * Creates a new {@link EventHandlingResult} to indicate that the value handling is failed and no need a retry
     *
     * @param failureDescription
     * @param cause
     * @return
     */
    public static EventHandlingResult createFailedResult(@Nonnull String failureDescription, Exception cause) {
        return new EventHandlingResult(failureDescription, cause, false, false, 0, 0, false);
    }

    /**
     * Creates a new {@link EventHandlingResult} to indicate that the value handling is failed and should be
     * retried several times immediately in this execution of the {@link EventHandler}.
     *
     * @param retryDescription
     * @param cause
     * @param suggestedRetryCount Number of Retries
     * @param suggestedRetryTimeout Timeout for each retry
     * @return
     */
    public static EventHandlingResult createRetryNowResult(@Nonnull String retryDescription, Exception cause,
                                                           @Nonnegative int suggestedRetryCount, @Nonnegative int suggestedRetryTimeout) {
        return new EventHandlingResult(retryDescription, cause, false, true, suggestedRetryCount, suggestedRetryTimeout, false);
    }

    /**
     * Creates a new {@link EventHandlingResult} to indicate that the value handling is failed and should be
     * retried several times immediately in this execution or next execution of the {@link EventHandler}.
     *
     * @param retryDescription
     * @param cause
     * @param suggestedRetryCount Number of Retries
     * @param suggestedRetryTimeout Timeout for each retry
     * @return
     */
    public static EventHandlingResult createRetryNowOrLaterResult(@Nonnull String retryDescription, Exception cause,
                                                                  @Nonnegative int suggestedRetryCount,
                                                                  @Nonnegative int suggestedRetryTimeout) {
        return new EventHandlingResult(retryDescription, cause, false, true, suggestedRetryCount, suggestedRetryTimeout, true);
    }

    /**
     * Creates a new {@link EventHandlingResult} to indicate that the value handling is failed and should be
     * retried in next execution of the {@link EventHandler}.
     *
     * @param retryDescription
     * @param cause
     * @return
     */
    public static EventHandlingResult createRetryLaterResult(@Nonnull String retryDescription, Exception cause) {
        return new EventHandlingResult(retryDescription, cause, false, false, 0, 0, true);
    }

    private final String description;

    private final Exception cause;
    private final boolean successful;
    private final boolean retryImmediately;
    private final int retryCount;
    private final int retryTimeout;
    private final boolean retryLater;

    protected EventHandlingResult(@Nonnull String description, Exception cause, boolean successful,
                                  boolean retryImmediately, @Nonnegative int retryCount, @Nonnegative int retryTimeout,
                                  boolean retryLater) {
        this.description = description;

        this.cause = cause;

        this.successful = successful;

        this.retryImmediately = retryImmediately;
        if (retryCount < 0)
            this.retryCount = 0;
        else
            this.retryCount = retryCount;

        if (retryTimeout < 0)
            this.retryTimeout = 0;
        else
            this.retryTimeout = retryTimeout;

        this.retryLater = retryLater;
    }

    public String getDescription() {
        return description;
    }

    public Exception getCause() {
        return cause;
    }

    /**
     * Checks if the value handling can be considered done, whether it was successful or not.
     * <p>
     * Finished handling implies that the value doesn't need a retry,
     * either {@link #isRetryableImmediately() immediate} or {@link #isRetryableLater() delayed}.
     *
     * @return true if the value is handled and can be discarded, false if it has to be retried
     */
    public boolean isHandled() {
        return !(retryImmediately || retryLater);
    }

    /**
     * Checks if the value handling was successful or not.
     * <p>
     * Success implies that the value {@link #isHandled() is handled}.
     *
     * @return true upon success, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Checks if the value handling can be retried during this execution of the {@link EventHandler}.
     * Retry implies {@link #isSuccessful() failure} and {@link #isHandled() unfinished handling}.
     * Does not include {@link #isRetryableImmediately(int) retry count}.
     *
     * @return true if the value handling should be retried during this execution of the {@link EventHandler}, false otherwise
     */
    public boolean isRetryableImmediately() {
        return retryImmediately;
    }

    /**
     * Checks if the value handling should be retried during this execution of the {@link EventHandler}.
     * <p>
     * Retry implies {@link #isSuccessful() failure} and {@link #isHandled() unfinished handling}.
     *
     * @param currentRetries number of retries performed that will be checked against
     * {@link #getSuggestedRetries() suggested retry count}
     * @return true if the value handling should be retried during this execution of the {@link EventHandler}, false otherwise
     */
    public boolean isRetryableImmediately(int currentRetries) {
        return isRetryableImmediately() && (currentRetries < retryCount);
    }

    /**
     * Get the suggested retry times from value handling value
     *
     * @return the number of {@link #isRetryableImmediately() immediate retries} that the value suggests,
     * zero if immediate retries are not required
     */
    public int getSuggestedRetries() {
        return retryCount;
    }

    /**
     * Get the suggested timeout for each retry from value handling value
     *
     * @return the suggested time in milliseconds between {@link #isRetryableImmediately() immediate retries},
     * zero if immediate retries are not needed or can be done without timeout
     */
    public int getSuggestedRetryTimeout() {
        return retryTimeout;
    }

    /**
     * Checks if the value handling can be retried during next execution of the {@link EventHandler}.
     * Retry should imply {@link #isSuccessful() failure} and {@link #isHandled() finished handling}.
     *
     * @return true if the value should be retried in next execution of the {@link EventHandler}, false otherwise
     */
    public boolean isRetryableLater() {
        return retryLater;
    }

    public SimpleResult getSimpleResult() {
        if(isSuccessful())
            return SUCCESS;
        else if (isRetryableLater())
            return POSTPONED;
        else
            return FAILED;
    }
    @Override
    public String toString() {
        return description;
    }

    enum SimpleResult {
        SUCCESS,
        POSTPONED,
        FAILED
    }

}
