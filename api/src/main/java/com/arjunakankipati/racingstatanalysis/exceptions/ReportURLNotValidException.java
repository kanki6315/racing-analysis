package com.arjunakankipati.racingstatanalysis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a report URL is not valid or the data format doesn't match the expected structure.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ReportURLNotValidException extends RuntimeException {

    /**
     * Constructs a new ReportURLNotValidException with no detail message.
     */
    public ReportURLNotValidException() {
        super();
    }

    /**
     * Constructs a new ReportURLNotValidException with the specified detail message.
     *
     * @param message the detail message
     */
    public ReportURLNotValidException(String message) {
        super(message);
    }

    /**
     * Constructs a new ReportURLNotValidException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ReportURLNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}