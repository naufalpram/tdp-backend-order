package com.edts.tdp.batch4.controller;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.edts.tdp.batch4.bean.request.RequestProductBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.bean.response.FullOrderInfoBean;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.service.OrderLogicService;
import com.edts.tdp.batch4.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> createOrder(@RequestBody List<RequestProductBean> body, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        // validate customer

        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.createOrder(body, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrders(@RequestParam int page,
                                                                            @RequestParam int size,
                                                                            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<Page<CreatedOrderBean>> response;
        response = orderService.getAllOrderByCustomerId(page, size, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history/filter")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrdersByStatus(@RequestParam String status,
                                                                                    @RequestParam int page,
                                                                                    @RequestParam int size,
                                                                                    HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<Page<CreatedOrderBean>> responseBean;
        responseBean = orderService.findAllByCustomerIdAndStatus(status, page, size, httpServletRequest);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @PostMapping("/update/sent")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> updateOrderSent(@RequestParam String orderNumber,
                                                                              HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.sendOrder(orderNumber, httpServletRequest);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/cancel")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> cancelOrder(@RequestParam String orderNumber,
                                                                          HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.cancelOrder(orderNumber, httpServletRequest);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/return")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> returnOrder(@RequestParam String orderNumber,
                                                                          HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.returnOrder(orderNumber, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponseBean<FullOrderInfoBean>> getFullOrderInfo(@RequestParam String orderNumber,
                                                                                HttpServletRequest httpServletRequest) {
        OrderCustomerInfo orderCustomerInfo;
        try {
            orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest);
        } catch (Exception e) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), "/detail");
        }
        BaseResponseBean<FullOrderInfoBean> response;
        response = orderService.getFullOrderInfo(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/generate-report/{status}")
    public ResponseEntity<BaseResponseBean<String>> generateOrderReport(@PathVariable String status) throws IOException {
        BaseResponseBean<String> response = orderService.generateCsvReport(status);
        return new ResponseEntity<>(response, HttpStatus.OK);   
    }
}
