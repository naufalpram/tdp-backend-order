package com.edts.tdp.batch4.bean.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class OrderCustomExceptionBean {
    private int errorCode;
    private HttpStatus status;
    private String message;
    private String path;
}
