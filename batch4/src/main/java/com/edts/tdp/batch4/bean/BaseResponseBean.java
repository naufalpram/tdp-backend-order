package com.edts.tdp.batch4.bean;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponseBean {
    private HttpStatus status;
    private int code;
    private String message;
    private String timestamp;
}
