package com.edts.tdp.batch4.controller;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.request.RequestProductBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.service.OrderLogicService;
import com.edts.tdp.batch4.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderLogicService orderLogicService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> createOrder(@RequestBody List<RequestProductBean> body){
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();
        response = orderService.createOrder(body);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<OrderHeader>>> getAllOrders(@RequestParam Long customerId,
                                                                            @RequestParam int page,
                                                                            @RequestParam int size) {
        BaseResponseBean<Page<OrderHeader>> response;
        response = orderService.getAllOrderByCustomerId(customerId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-history")
    public ResponseEntity<BaseResponseBean<Page<OrderHeader>>> getAllOrdersByStatus(@RequestParam long customerId,
                                                                           @RequestParam String status,
                                                                           @RequestParam int page,
                                                                           @RequestParam int size) {
        BaseResponseBean<Page<OrderHeader>> responseBean;
        responseBean = orderService.findAllByCustomerIdAndStatus(customerId, status, page, size);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @PostMapping("/update/sent")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> updateOrderSent(@RequestParam long customerId,
                                                                              @RequestParam String orderNumber) {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.updateHistoryStatus(customerId, orderNumber);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }

    @PostMapping("update/cancel")
    public ResponseEntity<BaseResponseBean<CreatedOrderBean>> cancelOrder(@RequestParam long customerId,
                                                                          @RequestParam String orderNumber) {
        BaseResponseBean<CreatedOrderBean> orderBean;
        orderBean = orderService.cancelOrder(customerId, orderNumber);
        return new ResponseEntity<>(orderBean, HttpStatus.OK);
    }
}
