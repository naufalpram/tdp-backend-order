package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.bean.response.FullOrderInfoBean;
import com.edts.tdp.batch4.bean.response.OrderDetailBean;
import com.edts.tdp.batch4.constant.Status;
import com.edts.tdp.batch4.bean.request.RequestProductBean;
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

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public BaseResponseBean<CreatedOrderBean> createOrder(List<RequestProductBean> body) {
        String path = "/create";
        if (body.isEmpty()) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Body length can't be zero", path);
        }
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();
        DecimalFormat priceFormat = new DecimalFormat("#.##");
        DecimalFormat distanceFormat = new DecimalFormat("#.00");
        //double distance = OrderLogicService.distanceCounter(-6.175205678775132, 106.82715894445303);
        double distance = OrderLogicService.distanceCounter(-8.498191844703317, 140.4021760551087);

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomerId(24L);
        orderHeader.setCreatedBy("amini");
        orderHeader.setTotalPaid(0.0);
        orderHeader.setModifiedBy("amini dwi");
        orderHeader.setTotalPaid(OrderLogicService.deliveryCost(distance));
        Optional<OrderHeader> cek = this.orderHeaderRepository.findByOrderNumber(orderHeader.getOrderNumber());

        if (cek.isPresent())
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, String.format("Order with orderNumber: %s is present in the database", orderHeader.getOrderNumber()), path);

        OrderHeader tempOrderHeader = this.orderHeaderRepository.save(orderHeader);

        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedBy("amini");
        orderDelivery.setStreet("jalan sudirman");
        orderDelivery.setProvince("Jakarta Pusat");
        orderDelivery.setDistanceInKm(Double.parseDouble(distanceFormat.format(distance)));
        orderDelivery.setLatitude(-6.175205678775132);
        orderDelivery.setLongitude(106.82715894445303);


        ArrayList<OrderDetail> arr = new ArrayList<>();
        double totalPrice = 0.0;
        for (RequestProductBean item : body) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductId(item.getProductId());
            orderDetail.setPrice(item.getPrice());
            orderDetail.setQty(item.getQty());
            orderDetail.setCreatedBy("amini");
            orderDetail.setOrderHeader(tempOrderHeader);

            arr.add(orderDetail);
            totalPrice += (item.getPrice() * item.getQty());
        }
        this.orderDetailRepository.saveAll(arr);

        tempOrderHeader.setTotalPaid(Double.valueOf(priceFormat.format(tempOrderHeader.getTotalPaid() + totalPrice)));
        tempOrderHeader = this.orderHeaderRepository.save(tempOrderHeader);

        orderDelivery.setOrderHeader(tempOrderHeader);
        this.orderDeliveryRepository.save(orderDelivery);

        CreatedOrderBean orderBean = new CreatedOrderBean();
        orderBean.setCreatedAt(tempOrderHeader.getCreatedAt());
        orderBean.setOrderNumber(tempOrderHeader.getOrderNumber());
        orderBean.setTotalPaid(String.format("%.2f", tempOrderHeader.getTotalPaid()));
        orderBean.setStatus(tempOrderHeader.getStatus());

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        response.setCode(200);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(orderBean);
        return response;
    }

    public BaseResponseBean<Page<CreatedOrderBean>> getAllOrderByCustomerId(Long customerId, int page, int size) {
        String path = "/get-history";
        BaseResponseBean<Page<CreatedOrderBean>> response = new BaseResponseBean<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
        if (page < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid page: must be a 0 or a positive int", path);

        // get all orderHeader
        Page<CreatedOrderBean> orders = this.orderHeaderRepository.findAllByCustomerId(customerId, pageable);
        if (orders.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND,
                    String.format("Order history request with customerId: %d is empty", customerId), path);

        response.setStatus(HttpStatus.OK);
        response.setCode(200);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(orders);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public BaseResponseBean<Page<CreatedOrderBean>> findAllByCustomerIdAndStatus(long customerId,
                                                                            String status,
                                                                            int page,
                                                                            int size) {
        String path = "/get-history/filter";
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        BaseResponseBean<Page<CreatedOrderBean>> response = new BaseResponseBean<>();

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
        if (page < 0)
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid page: must be a 0 or a positive int", path);

        Page<CreatedOrderBean> headerData = this.orderHeaderRepository.findAllByCustomerIdAndStatus(customerId, status, pageable);
        if (headerData.isEmpty()) {
            throw new OrderCustomException(HttpStatus.NO_CONTENT, "No Content Data", path);
        }
            response.setStatus(HttpStatus.OK);
            response.setData(headerData);
            response.setMessage("Success");
            response.setCode(200);
            response.setTimestamp(LocalDateTime.now());
            return response;
    }

    public BaseResponseBean<CreatedOrderBean> sendOrder(long customerId, String orderNumber) {
        String path = "/update/sent";
        BaseResponseBean<CreatedOrderBean> orderBean = new BaseResponseBean<>();

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);

        Optional<OrderHeader> headerData = this.orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);
        if (headerData.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With Order Number: %s is not found", orderNumber), path);

        OrderHeader orderHeader = headerData.get();
        if (!orderHeader.getStatus().equals(Status.ORDERED)) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not send order with status : " + orderHeader.getStatus(), path);
        }
        orderHeader.setStatus(Status.SENT);
        orderHeader.setModifiedAt(LocalDateTime.now());
        orderHeader.setModifiedBy("Admin");
        this.orderHeaderRepository.save(orderHeader);

        CreatedOrderBean createdOrderBean = new CreatedOrderBean();
        createdOrderBean.setStatus(Status.SENT);
        createdOrderBean.setOrderNumber(orderNumber);
        createdOrderBean.setCreatedAt(orderHeader.getCreatedAt());
        createdOrderBean.setModifiedAt(LocalDateTime.now());

        orderBean.setStatus(HttpStatus.OK);
        orderBean.setData(createdOrderBean);
        orderBean.setMessage(HttpStatus.OK.getReasonPhrase());
        orderBean.setCode(200);
        orderBean.setTimestamp(LocalDateTime.now());

        return orderBean;
    }

    public BaseResponseBean<CreatedOrderBean> cancelOrder(long customerId,
                                                          String orderNumber) {
        String path = "update/cancel";
        BaseResponseBean<CreatedOrderBean> orderBean = new BaseResponseBean<>();

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);

        Optional<OrderHeader> headerData = this.orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);
        if (headerData.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With Order Number: %s is not found", orderNumber), path);

        OrderHeader orderHeader = headerData.get();
        if (!orderHeader.getStatus().equals(Status.ORDERED))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not cancel order with status: " + orderHeader.getStatus(), path);
        if (orderHeader.getOrderDelivery().isDelivered())
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not cancel a delivered order", path);


        orderHeader.setStatus(Status.CANCELLED);
        orderHeader.setModifiedAt(LocalDateTime.now());
        orderHeader.setModifiedBy(orderHeader.getCreatedBy());
        this.orderHeaderRepository.save(orderHeader);

        CreatedOrderBean createdOrderBean = new CreatedOrderBean();
        createdOrderBean.setStatus(Status.CANCELLED);
        createdOrderBean.setOrderNumber(orderNumber);
        createdOrderBean.setCreatedAt(orderHeader.getCreatedAt());
        createdOrderBean.setModifiedAt(LocalDateTime.now());

        orderBean.setStatus(HttpStatus.OK);
        orderBean.setData(createdOrderBean);
        orderBean.setMessage(HttpStatus.OK.getReasonPhrase());
        orderBean.setCode(200);
        orderBean.setTimestamp(LocalDateTime.now());
        return orderBean;
    }

    public BaseResponseBean<CreatedOrderBean> returnOrder(long customerId,
                                                          String orderNumber) {
        String path = "update/return";
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);

        Optional<OrderHeader> orderHeaderData = this.orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);
        if (orderHeaderData.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With order number: %s is not found", orderNumber), path);

        OrderHeader orderHeader = orderHeaderData.get();
        if (!orderHeader.getStatus().equals(Status.DELIVERED))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not return order with status: %s" + orderHeader.getStatus(), path);
        if (!orderHeader.getOrderDelivery().isDelivered())
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not return an order that is not yet delivered", path);

        orderHeader.setStatus(Status.RETURNED);
        orderHeader.setModifiedAt(LocalDateTime.now());
        orderHeader.setModifiedBy(orderHeader.getCreatedBy());
        this.orderHeaderRepository.save(orderHeader);

        CreatedOrderBean createdOrderBean = new CreatedOrderBean();
        createdOrderBean.setStatus(Status.RETURNED);
        createdOrderBean.setOrderNumber(orderNumber);
        createdOrderBean.setCreatedAt(orderHeader.getCreatedAt());
        createdOrderBean.setModifiedAt(LocalDateTime.now());

        response.setStatus(HttpStatus.OK);
        response.setData(createdOrderBean);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public BaseResponseBean<FullOrderInfoBean> getFullOrderInfo(Long customerId, String orderNumber) {
        String path = "/detail";
        BaseResponseBean<FullOrderInfoBean> response = new BaseResponseBean<>();
        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);

        Optional<OrderHeader> orderHeader = this.orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);
        if (orderHeader.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With order number: %s is not found", orderNumber), path);

        OrderHeader data = orderHeader.get();
        FullOrderInfoBean infoBean = new FullOrderInfoBean();
        infoBean.setId(data.getId());
        infoBean.setCreatedAt(data.getCreatedAt());
        infoBean.setCustomerId(data.getCustomerId());
        infoBean.setOrderNumber(data.getOrderNumber());
        infoBean.setTotalPaid(String.format("%.2f", data.getTotalPaid()));
        infoBean.setStatus(data.getStatus());

        List<OrderDetail> orderDetailList = data.getOrderDetailList();
        List<OrderDetailBean> detailsBean = new ArrayList<>();
        for (OrderDetail item : orderDetailList) {
            OrderDetailBean orderDetailBean = new OrderDetailBean();
            orderDetailBean.setId(item.getId());
            orderDetailBean.setProductId(item.getProductId());
            orderDetailBean.setQty(item.getQty());
            orderDetailBean.setPrice(String.format("%.2f", item.getPrice()));
            detailsBean.add(orderDetailBean);
        }
        infoBean.setOrderDetailList(detailsBean);

        response.setStatus(HttpStatus.OK);
        response.setData(infoBean);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
