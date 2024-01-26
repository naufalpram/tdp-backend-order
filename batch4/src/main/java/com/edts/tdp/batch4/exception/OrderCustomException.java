package com.edts.tdp.batch4.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderCustomException extends RuntimeException {
    private final HttpStatus status;
    private final String path;
    public OrderCustomException(HttpStatus status, String message, String path) {
        super(message);
        this.status = status;
        this.path = path;
    }
}
