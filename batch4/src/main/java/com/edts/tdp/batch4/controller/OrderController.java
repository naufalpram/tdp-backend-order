package com.edts.tdp.batch4.controller;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.response.AllOrderPageBean;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.service.OrderLogicService;
import com.edts.tdp.batch4.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    private OrderLogicService orderLogicService;

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<OrderHeader>>> showCustomerHistoryByStatus(@RequestParam long customerId,
                                                                           @RequestParam String status,
                                                                           @RequestParam int pageNumber,
                                                                           @RequestParam int size) {
        BaseResponseBean<Page<OrderHeader>> responseBean;
        responseBean = orderService.findAllByCustomerIdAndStatus(customerId, status, pageNumber, size);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }
}
