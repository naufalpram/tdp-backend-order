package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderHeaderRepository orderHeaderRepository;
//    public List<?> fetchListOrderByStatus(String status) {
////        List<OrderHeader> orderHeadersList =
//    }
}
