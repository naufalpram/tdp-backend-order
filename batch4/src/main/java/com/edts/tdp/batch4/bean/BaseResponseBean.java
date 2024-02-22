package com.edts.tdp.batch4.bean;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class BaseResponseBean<T> {
    private HttpStatus status;
    private int code;
    private String message;
    private LocalDateTime timestamp;
    private T data;
}
