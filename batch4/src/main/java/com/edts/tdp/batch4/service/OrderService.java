package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.request.RequestProductBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderDelivery;
import com.edts.tdp.batch4.model.OrderDetail;
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
import java.util.ArrayList;
import java.util.List;

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

    public BaseResponseBean<CreatedOrderBean> createOrder(List<RequestProductBean> body){
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomerId(24L);
        orderHeader.setCreatedBy("amini");
        orderHeader.setTotalPaid(0.0);
        orderHeader.setModifiedBy("amini dwi");
        OrderHeader orderHeader1 = this.orderHeaderRepository.save(orderHeader);

        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedBy("amini");
        orderDelivery.setStreet("jalan sudirman");
        orderDelivery.setProvince("Jakarta Pusat");
        orderDelivery.setDistanceInKm(1.0);
        orderDelivery.setLatitude(-6.175205678775132);
        orderDelivery.setLongitude(106.82715894445303);

        ArrayList<OrderDetail> arr = new ArrayList<>();
        for (int i = 0; i < body.size(); i++) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductId(body.get(i).getProductId());
            orderDetail.setPrice(body.get(i).getPrice());
            orderDetail.setQty(body.get(i).getQty());
            orderDetail.setCreatedBy("amini");
            orderDetail.setOrderHeader(orderHeader);

            arr.add(orderDetail);
        }
        this.orderDetailRepository.saveAll(arr);


//        OrderDetail orderDetail1 = new OrderDetail();
//        orderDetail1.setCreatedBy("amini");
//        orderDetail1.setQty(0);
//        orderDetail1.setPrice(0);
//
//        OrderDetail orderDetail2 = new OrderDetail();
//        orderDetail2.setCreatedBy("amini");
//        orderDetail2.setQty(0);
//        orderDetail2.setPrice(0);
//
//        OrderDetail orderDetail3 = new OrderDetail();
//        orderDetail3.setCreatedBy("amini");
//        orderDetail3.setQty(0);
//        orderDetail3.setPrice(0);

        orderDelivery.setOrderHeader(orderHeader1);
//        orderDetail1.setOrderHeader(orderHeader1);
//        orderDetail2.setOrderHeader(orderHeader1);
//        orderDetail3.setOrderHeader(orderHeader1);

        this.orderDeliveryRepository.save(orderDelivery);
//        this.orderDetailRepository.save(orderDetail1);
//        this.orderDetailRepository.save(orderDetail2);
//        this.orderDetailRepository.save(orderDetail3);

        //response.setMessage();
        CreatedOrderBean orderBean = new CreatedOrderBean();
        orderBean.setCreatedAt(orderHeader1.getCreatedAt());
        orderBean.setOrderNumber(orderHeader1.getOrderNumber());
        orderBean.setTotalPaid(orderHeader1.getTotalPaid());
        orderBean.setStatus(orderHeader1.getStatus());

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        response.setCode(200);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(orderBean);
        return response;
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
    public BaseResponseBean<Page<OrderHeader>> findAllByCustomerIdAndStatus(long customerId,
                                                          String status,
                                                          int page,
                                                          int size) {
        String path = "/get-history";
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        BaseResponseBean<Page<OrderHeader>> response = new BaseResponseBean<>();
        if ( customerId < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
        if ( page < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Page Number", path);
        Page<OrderHeader> headerData = orderHeaderRepository.findAllByCustomerIdAndStatus(customerId,status,pageable);

        if ( headerData.isEmpty() ) {
            throw new OrderCustomException(HttpStatus.NO_CONTENT, "No Content Data", path);
        } else {
            response.setStatus(HttpStatus.OK);
            response.setData(headerData);
            response.setMessage("Success");
            response.setCode(200);
            response.setTimestamp(LocalDateTime.now());
        }
        return response;
    }
}
