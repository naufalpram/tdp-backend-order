package com.edts.tdp.batch4.controller;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<OrderHeader>>> getAllOrders(@RequestParam Long customerId, @RequestParam int page, @RequestParam int size) {
        BaseResponseBean<Page<OrderHeader>> response;
        response = orderService.getAllOrderByCustomerId(customerId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
