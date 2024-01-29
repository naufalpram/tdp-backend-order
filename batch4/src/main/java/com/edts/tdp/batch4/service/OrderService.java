package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;

    @Autowired
    public OrderService(OrderHeaderRepository orderHeaderRepository,
                        OrderDetailRepository orderDetailRepository,
                        OrderDeliveryRepository orderDeliveryRepository) {
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
    }

    public BaseResponseBean<Page<OrderHeader>> getAllOrderByCustomerId(Long customerId, int page, int size) {
        String path = "/order/get-history";
        BaseResponseBean<Page<OrderHeader>> response = new BaseResponseBean<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST,
                "Customer id must be a positive integer", path);
        if (page < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST,
                "Invalid page: must be a 0 or a positive int", path);

        // get all orderHeader
        Page<OrderHeader> orders = this.orderHeaderRepository.findAllByCustomerId(customerId, pageable);

        if (orders.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND,
                    String.format("Order history request with customerId: %d is empty", customerId), path);
        else {
            response.setStatus(HttpStatus.OK);
            response.setCode(200);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setData(orders);
            response.setTimestamp(LocalDateTime.now());
        }
        return response;
    }
}
