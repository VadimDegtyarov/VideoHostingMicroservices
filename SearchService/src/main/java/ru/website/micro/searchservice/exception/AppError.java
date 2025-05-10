package ru.website.micro.searchservice.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppError {
    private int statusCode;
    private String message;

    public AppError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
