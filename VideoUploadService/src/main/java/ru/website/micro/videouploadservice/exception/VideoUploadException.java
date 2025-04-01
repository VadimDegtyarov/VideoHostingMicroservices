package ru.website.micro.videouploadservice.exception;

import org.springframework.http.HttpStatus;

import java.io.IOException;

public class VideoUploadException extends RuntimeException {
    private final HttpStatus httpStatus;

    public VideoUploadException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public VideoUploadException(String message, IOException e) {
        super(message, e);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    public VideoUploadException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public VideoUploadException(String videoProcessingFailed, HttpStatus httpStatus, Throwable e) {
        super(videoProcessingFailed, e);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDetailsMessage() {
        return String.format("Error [%s]: %s Cause: %s", httpStatus, getMessage(), getCause() != null ? getCause().getMessage() : "none");
    }
}
