package ru.bookingsystem.exception;

public class BadRequestParametersException extends RuntimeException {
    public BadRequestParametersException(String message) {
        super(message);
    }
}
