package com.prabesh.growphasetech.exceptions;

/**
 * Base unchecked exception for all Nepali date library errors.
 *
 * <p>Using a custom root exception allows library consumers to catch all
 * library-specific errors with a single {@code catch} block:</p>
 *
 * <pre>{@code
 * try {
 *     NepaliDate date = NepaliDate.parse("invalid");
 * } catch (NepaliDateException e) {
 *     // handle any library error
 * }
 * }</pre>
 *
 * @see UnsupportedBSYearException
 * @see InvalidNepaliDateException
 */
public class NepaliDateException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message human-readable description of what went wrong
     */
    public NepaliDateException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message human-readable description of what went wrong
     * @param cause   the underlying exception that triggered this error
     */
    public NepaliDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
