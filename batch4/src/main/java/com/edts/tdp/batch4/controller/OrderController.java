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
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> createOrder(@RequestBody List<RequestProductBean> body,
                                                                          HttpServletRequest httpServletRequest) {
        // validate token

        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/create");
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.createOrder(body, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrders(@RequestParam int page,
                                                                            @RequestParam int size,
                                                                            HttpServletRequest httpServletRequest) {
        // validate token
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/get-history");
        BaseResponseBean<Page<CreatedOrderBean>> response;
        response = orderService.getAllOrderByCustomerId(page, size, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history/filter")
    public ResponseEntity<BaseResponseBean<Page<CreatedOrderBean>>> getAllOrdersByStatus(@RequestBody Map<String, String> request,
                                                                                    @RequestParam int page,
                                                                                    @RequestParam int size,
                                                                                    HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("status"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain status key", "/get-history/filter");
        String status = request.get("status");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/get-history/filter");
        BaseResponseBean<Page<CreatedOrderBean>> responseBean;
        responseBean = orderService.findAllByCustomerIdAndStatus(status, page, size, orderCustomerInfo);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @PostMapping("/update/sent")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> updateOrderSent(@RequestBody Map<String, String> request,
                                                                              HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("orderNumber"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain orderNumber key", "/update/sent");
        String orderNumber = request.get("orderNumber");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/update/sent");
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.sendOrder(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/cancel")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> cancelOrder(@RequestBody Map<String, String> request,
                                                                          HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("orderNumber"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain orderNumber key", "/update/cancel");
        String orderNumber = request.get("orderNumber");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/update/cancel");
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.cancelOrder(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("/update/return")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> returnOrder(@RequestBody Map<String, String> request,
                                                                          HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("orderNumber"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain orderNumber key", "/update/return");
        String orderNumber = request.get("orderNumber");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/update/return");
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.returnOrder(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponseBean<FullOrderInfoBean>> getFullOrderInfo(@RequestBody Map<String, String> request,
                                                                                HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("orderNumber"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain orderNumber key", "/detail");
        String orderNumber = request.get("orderNumber");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/detail");
        BaseResponseBean<FullOrderInfoBean> response;
        response = orderService.getFullOrderInfo(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/generate-report/{status}")
    public ResponseEntity<BaseResponseBean<String>> generateOrderReport(@PathVariable String status) throws IOException {
        BaseResponseBean<String> response = orderService.generateCsvReport(status);
        return new ResponseEntity<>(response, HttpStatus.OK);   
    }

    @PostMapping("/update/delivered")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> updateOrderDelivered(@RequestBody Map<String, String> request,
                                                                                   HttpServletRequest httpServletRequest) {
        // validate token
        if (!request.containsKey("orderNumber"))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body does not contain orderNumber key", "/update/delivered");
        String orderNumber = request.get("orderNumber");
        OrderCustomerInfo orderCustomerInfo = OrderLogicService.getCustomerInfo(httpServletRequest, "/update/delivered");
        BaseResponseBean<CreatedOrderBean> response;
        response = orderService.updateOrderDelivered(orderNumber, orderCustomerInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
