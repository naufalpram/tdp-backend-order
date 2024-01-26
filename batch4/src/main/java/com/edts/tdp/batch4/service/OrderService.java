package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService{
    @Autowired
    private OrderHeaderRepository orderHeaderRepository;
    private OrderDetailRepository orderDetailRepository;
    private OrderDeliveryRepository orderDeliveryRepository;

    public Page<OrderHeader> findAllByCustomerIdAndStatus(long customerId,
                                                          String status,
                                                          int pageNumber,
                                                          int size) {
        Pageable pageable = PageRequest.of(pageNumber,size);
        return orderHeaderRepository.findAllByCustomerIdAndStatus(customerId,status,pageable);
    }
}
