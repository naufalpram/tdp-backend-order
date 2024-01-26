package com.edts.tdp.batch4.exception.handler;

import com.edts.tdp.batch4.bean.exception.OrderCustomExceptionBean;
import com.edts.tdp.batch4.exception.OrderCustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class OrderCustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrderCustomException.class)
    public ResponseEntity<?> customException(OrderCustomException e) {
        return ResponseEntity.status(e.getStatus()).body(new OrderCustomExceptionBean(
                e.getStatus().value(), e.getStatus(), e.getMessage(), e.getPath()));
    }
}
