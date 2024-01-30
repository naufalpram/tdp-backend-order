package com.edts.tdp.batch4.controller;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.request.RequestProductBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.bean.response.FullOrderInfoBean;
import com.edts.tdp.batch4.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> createOrder(@RequestBody List<RequestProductBean> body, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.createOrder(body, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrders(@RequestParam Long customerId,
                                                                            @RequestParam int page,
                                                                            @RequestParam int size) {
        BaseResponseBean<Page<CreatedOrderBean>> response;
        response = orderService.getAllOrderByCustomerId(customerId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history/filter")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrdersByStatus(@RequestParam long customerId,
                                                                           @RequestParam String status,
                                                                           @RequestParam int page,
                                                                           @RequestParam int size) {
        BaseResponseBean<Page<CreatedOrderBean>> responseBean;
        responseBean = orderService.findAllByCustomerIdAndStatus(customerId, status, page, size);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @PostMapping("/update/sent")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> updateOrderSent(@RequestParam long customerId,
                                                                              @RequestParam String orderNumber) {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.sendOrder(customerId, orderNumber);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/cancel")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> cancelOrder(@RequestParam long customerId,
                                                                          @RequestParam String orderNumber) {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.cancelOrder(customerId, orderNumber);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/return")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> returnOrder(@RequestParam long customerId,
                                                                          @RequestParam String orderNumber) {
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.returnOrder(customerId, orderNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponseBean<FullOrderInfoBean>> getFullOrderInfo(@RequestParam Long customerId,
                                                                                @RequestParam String orderNumber) {
        BaseResponseBean<FullOrderInfoBean> response;
        response = orderService.getFullOrderInfo(customerId, orderNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-invoice/{orderNumber}")
    public ResponseEntity<BaseResponseBean<ClassPathResource>> getOrderInvoice(@PathVariable String orderNumber, HttpServletRequest httpServletRequest) {
        BaseResponseBean<ClassPathResource> response = new BaseResponseBean<>();

        // Build the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
//        headers.setContentLength(resource.contentLength());


        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
