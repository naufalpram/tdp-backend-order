package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.response.AllOrderPageBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.constant.Status;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService{
    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;

    public OrderService(OrderHeaderRepository orderHeaderRepository,
                        OrderDetailRepository orderDetailRepository,
                        OrderDeliveryRepository orderDeliveryRepository) {
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
    }

    public BaseResponseBean<Page<OrderHeader>> findAllHistoryByCustomerIdAndStatus(long customerId,
                                                          String status,
                                                          int pageNumber,
                                                          int size) {
        String path = "/get-history";
        Pageable page = PageRequest.of(pageNumber,size, Sort.by("createdAt").descending());
        BaseResponseBean<Page<OrderHeader>> response = new BaseResponseBean<>();
        if ( customerId < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
        if ( pageNumber < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Page Number", path);
        Page<OrderHeader> headerData = orderHeaderRepository.findAllByCustomerIdAndStatus(customerId,status,page);

        if ( headerData.isEmpty() ) {
            throw new OrderCustomException(HttpStatus.NO_CONTENT, "No Content Data", path);
        } else {
            response.setStatus(HttpStatus.OK);
            response.setData(headerData);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setCode(200);
            response.setTimestamp(LocalDateTime.now());
        }
        return response;
    }

    public BaseResponseBean<CreatedOrderBean> updateHistoryStatus(long customerId,
                                                                   String orderNumber) {
        String path = "/update/sent";
        BaseResponseBean<CreatedOrderBean> orderBean = new BaseResponseBean<>();
        if ( customerId < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
        Optional<OrderHeader> headerData = orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);

        if ( headerData.isEmpty() ) {
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With Order Number: %s is not found",orderNumber), path);
        } else {
            OrderHeader orderHeader = headerData.get();
            if ( !orderHeader.getStatus().equals("Ordered") ) {
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Order Number" + "Order : " + orderHeader.getStatus(), path);
            }
            orderHeader.setStatus(Status.SENT);
            orderHeader.setModifiedAt(LocalDateTime.now());
            orderHeader.setModifiedBy("Admin");
            orderHeader = orderHeaderRepository.save(orderHeader);
            CreatedOrderBean createdOrderBean = new CreatedOrderBean();
            createdOrderBean.setStatus(Status.SENT);
            createdOrderBean.setOrderNumber(orderNumber);
            createdOrderBean.setModifiedAt(LocalDateTime.now());
            orderBean.setStatus(HttpStatus.OK);
            orderBean.setData(createdOrderBean);
            orderBean.setMessage(HttpStatus.OK.getReasonPhrase());
            orderBean.setCode(200);
            orderBean.setTimestamp(LocalDateTime.now());
        }
        return orderBean;
    }
}
