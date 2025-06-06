package ru.website.micro.searchservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    public ResourceNotFoundException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
    }
    public ResourceNotFoundException(String message,Throwable throwable) {
        super(message,throwable);
        this.httpStatus = HttpStatus.NOT_FOUND;
    }
    public String getDetailsMessage() {
        return String.format("Error [%s]: %s Cause: %s", httpStatus, getMessage(), getCause() != null ? getCause().getMessage() : "none");
    }
}