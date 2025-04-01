package ru.website.micro.videouploadservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> catchResourceNotFoundException(ConfigDataResourceNotFoundException e){
        log.error(e.getMessage(),e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),e.getMessage()),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(VideoUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppError> handleVideoUpload(VideoUploadException  e){
        log.error(e.getMessage(),e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),e.getMessage()),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ImageUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppError> handleImageUpload(ImageUploadException  e){
        log.error(e.getMessage(),e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),e.getMessage()),HttpStatus.NOT_FOUND);
    }


}