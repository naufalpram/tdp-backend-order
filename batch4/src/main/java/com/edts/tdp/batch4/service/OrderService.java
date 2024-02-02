package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.catalog.OrderProductInfo;
import com.edts.tdp.batch4.bean.catalog.OrderProductResponse;
import com.edts.tdp.batch4.bean.customer.OrderCartBean;
import com.edts.tdp.batch4.bean.customer.OrderCustomerAddress;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.bean.response.FullOrderInfoBean;
import com.edts.tdp.batch4.bean.response.OrderDetailBean;
import com.edts.tdp.batch4.constant.Status;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.*;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;

    private final EmailService emailService;

    private final OrderLogicService orderLogicService;

    @Autowired
    public OrderService(OrderHeaderRepository orderHeaderRepository,
                        OrderDetailRepository orderDetailRepository,
                        OrderDeliveryRepository orderDeliveryRepository, EmailService emailService, OrderLogicService orderLogicService) {
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.emailService = emailService;
        this.orderLogicService = orderLogicService;
    }

    /**
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @param httpServletRequest to extract headers
     * @return BaseResponseBean with type CreatedOrderBean
     */
    public BaseResponseBean<CreatedOrderBean> createOrder(OrderCustomerInfo orderCustomerInfo, HttpServletRequest httpServletRequest) {
        String path = "/order/create";
        // fetch cart data from customer API and convert it to OrderCartBean
        List<LinkedHashMap<String, Integer>> cartData = orderLogicService.getCartData(httpServletRequest, path);
        List<OrderCartBean> cart = new ArrayList<>();
        for (LinkedHashMap<String, Integer> data : cartData) {
            OrderCartBean item = new OrderCartBean();
            item.setProductId(data.get("productId"));
            item.setQuantity(data.get("quantity"));
            cart.add(item);
        }
        if(cart.isEmpty()){
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Cart length can't be zero", path);
        }

        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();
        DecimalFormat priceFormat = new DecimalFormat("#.##");
        DecimalFormat distanceFormat = new DecimalFormat("#.00");

        OrderCustomerAddress orderCustomerAddress = this.orderLogicService.getCustomerAddress(orderCustomerInfo);
        // create order header
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomerId(orderCustomerInfo.getId());
        orderHeader.setCreatedBy(orderCustomerInfo.getUsername());
        double distance = this.orderLogicService.distanceCounter(orderCustomerAddress.getLatitude(), orderCustomerAddress.getLongitude());
        orderHeader.setTotalPaid(this.orderLogicService.deliveryCost(distance));
        Optional<OrderHeader> cek = this.orderHeaderRepository.findByOrderNumber(orderHeader.getOrderNumber());

        if (cek.isPresent())
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, String.format("Order with orderNumber: %s is present in the database", orderHeader.getOrderNumber()), path);

        OrderHeader tempOrderHeader = this.orderHeaderRepository.save(orderHeader);

        // create order delivery
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedBy(orderCustomerInfo.getUsername());
        orderDelivery.setStreet(orderCustomerAddress.getStreet());
        orderDelivery.setProvince(orderCustomerAddress.getProvince());
        orderDelivery.setPostCode(orderCustomerAddress.getPostcode());
        orderDelivery.setDistanceInKm(Double.parseDouble(distanceFormat.format(distance)));
        orderDelivery.setLatitude(orderCustomerAddress.getLatitude());
        orderDelivery.setLongitude(orderDelivery.getLongitude());

        // fetch product info of cart from catalog API
        List<Integer> temp = new ArrayList<>();
        for (OrderCartBean orderCartBean : cart) {
            temp.add((int) orderCartBean.getProductId());
        }
        OrderProductResponse orderProductResponse = this.orderLogicService.getAllProductInfo(temp);

        // create each order detail with a combination data of cart and product info
        ArrayList<OrderDetail> arr = new ArrayList<>();
        double totalPrice = 0.0;
        for (int i = 0; i < orderProductResponse.getData().size() ; i++) {
            OrderProductInfo item = orderProductResponse.getData().get(i);
            if ( item.getStock() < cart.get(i).getQuantity() ) {
                this.orderHeaderRepository.delete(tempOrderHeader);
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Stock of Product", path);
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductId(item.getId());
            orderDetail.setPrice(item.getPrice());
            orderDetail.setQty(cart.get(i).getQuantity());
            orderDetail.setCreatedBy(orderCustomerInfo.getUsername());
            orderDetail.setOrderHeader(tempOrderHeader);

            arr.add(orderDetail);
            totalPrice += (item.getPrice() * cart.get(i).getQuantity());
        }
        // if all product stock are empty
        if (arr.isEmpty()) {
            this.orderHeaderRepository.delete(tempOrderHeader);
            throw new OrderCustomException(HttpStatus.NO_CONTENT, "Empty products, please add product with available stock", path);
        }

        this.orderDetailRepository.saveAll(arr);

        // set total paid of order header
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

        // update stock of products and clear customer's cart
        this.orderLogicService.updateStockProduct(cart, true);
        orderLogicService.clearCartCustomer(httpServletRequest, path);
        return response;
    }

    /**
     * @param page page number
     * @param size page size
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean with type CreatedOrderBean
     */
    public BaseResponseBean<Page<CreatedOrderBean>> getAllOrderByCustomerId(int page, int size, OrderCustomerInfo orderCustomerInfo)  {
        String path = "/get-history";
        BaseResponseBean<Page<CreatedOrderBean>> response = new BaseResponseBean<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        long customerId = orderCustomerInfo.getId();

        if (page < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST,
                "Invalid page: must be a 0 or a positive int", path);

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

    /**
     * @param status filter by status
     * @param page page number
     * @param size page size
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean with type CreatedOrderBean
     */
    public BaseResponseBean<Page<CreatedOrderBean>> findAllByCustomerIdAndStatus(String status,
                                                                                 int page,
                                                                                 int size,
                                                                                 OrderCustomerInfo orderCustomerInfo) {
        String path = "/get-history/filter";
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());

        long customerId = orderCustomerInfo.getId();

        if ( page < 0 ) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Page Number", path);
        BaseResponseBean<Page<CreatedOrderBean>> response = new BaseResponseBean<>();

        if (customerId < 0) throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);

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

    /**
     * @param orderNumber unique order number
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean of type CreatedOrderBean
     */
    public BaseResponseBean<CreatedOrderBean> sendOrder(String orderNumber, OrderCustomerInfo orderCustomerInfo){

        long customerId = orderCustomerInfo.getId();

        String path = "/update/sent";
        BaseResponseBean<CreatedOrderBean> orderBean = new BaseResponseBean<>();

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
        createdOrderBean.setTotalPaid(String.format("%.2f", orderHeader.getTotalPaid()));

        orderBean.setStatus(HttpStatus.OK);
        orderBean.setData(createdOrderBean);
        orderBean.setMessage(HttpStatus.OK.getReasonPhrase());
        orderBean.setCode(200);
        orderBean.setTimestamp(LocalDateTime.now());

        return orderBean;
    }

    /**
     * @param orderNumber unique order number
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean of type CreatedOrderBean
     */
    public BaseResponseBean<CreatedOrderBean> cancelOrder(String orderNumber, OrderCustomerInfo orderCustomerInfo){
        String path = "update/cancel";
        BaseResponseBean<CreatedOrderBean> orderBean = new BaseResponseBean<>();

        long customerId = orderCustomerInfo.getId();

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

        // update stock via API

        CreatedOrderBean createdOrderBean = new CreatedOrderBean();
        createdOrderBean.setStatus(Status.CANCELLED);
        createdOrderBean.setOrderNumber(orderNumber);
        createdOrderBean.setCreatedAt(orderHeader.getCreatedAt());
        createdOrderBean.setModifiedAt(LocalDateTime.now());
        createdOrderBean.setTotalPaid(String.format("%.2f", orderHeader.getTotalPaid()));

        orderBean.setStatus(HttpStatus.OK);
        orderBean.setData(createdOrderBean);
        orderBean.setMessage(HttpStatus.OK.getReasonPhrase());
        orderBean.setCode(200);
        orderBean.setTimestamp(LocalDateTime.now());

        // update stock of products because of cancellation
        List<OrderCartBean> products = new ArrayList<>();
        for (OrderDetail item : orderHeader.getOrderDetailList()) {
            OrderCartBean cart = new OrderCartBean();
            cart.setProductId(item.getProductId());
            cart.setQuantity(item.getQty());
            products.add(cart);
        }
        this.orderLogicService.updateStockProduct(products, false);
        return orderBean;
    }

    /**
     * @param orderNumber unique order number
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean of type CreatedOrderBean
     */
    public BaseResponseBean<CreatedOrderBean> returnOrder(String orderNumber, OrderCustomerInfo orderCustomerInfo){
        String path = "update/return";
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();

        long customerId = orderCustomerInfo.getId();

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
        createdOrderBean.setTotalPaid(String.format("%.2f", orderHeader.getTotalPaid()));

        response.setStatus(HttpStatus.OK);
        response.setData(createdOrderBean);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());

        // update stock of products because of return
        List<OrderCartBean> products = new ArrayList<>();
        for (OrderDetail item : orderHeader.getOrderDetailList()) {
            OrderCartBean cart = new OrderCartBean();
            cart.setProductId(item.getProductId());
            cart.setQuantity(item.getQty());
            products.add(cart);
        }
        this.orderLogicService.updateStockProduct(products, false);
        return response;
    }

    /**
     * @param orderNumber unique order number
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean of type FullOrderInfoBean
     */
    public BaseResponseBean<FullOrderInfoBean> getFullOrderInfo(String orderNumber, OrderCustomerInfo orderCustomerInfo) {
        String path = "/detail";
        BaseResponseBean<FullOrderInfoBean> response = new BaseResponseBean<>();
        long customerId = orderCustomerInfo.getId();

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
        infoBean.setStreet(data.getOrderDelivery().getStreet());
        infoBean.setProvince(data.getOrderDelivery().getProvince());
        infoBean.setPostalCode(data.getOrderDelivery().getPostCode());
        infoBean.setDistanceInKm(data.getOrderDelivery().getDistanceInKm());

        // fetch product info from catalog API
        List<OrderDetail> orderDetailList = data.getOrderDetailList();
        List<Integer> temp = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            temp.add(Math.toIntExact(orderDetail.getProductId()));
        }
        OrderProductResponse orderProductResponse = this.orderLogicService.getAllProductInfo(temp);
        List<OrderDetailBean> detailsBean = getOrderDetailBeans(orderProductResponse, orderDetailList);
        infoBean.setOrderDetailList(detailsBean);

        response.setStatus(HttpStatus.OK);
        response.setData(infoBean);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * @param orderProductResponse bean to convert into orderDetailBean
     * @param orderDetailList orderDetails from orderHeader
     * @return List of OrderDetailBean
     */
    private static List<OrderDetailBean> getOrderDetailBeans(OrderProductResponse orderProductResponse, List<OrderDetail> orderDetailList) {
        List<OrderDetailBean> detailsBean = new ArrayList<>();
        for (int i = 0; i < orderProductResponse.getData().size(); i++) {
            OrderProductInfo item = orderProductResponse.getData().get(i);
            OrderDetailBean orderDetailBean = new OrderDetailBean();
            orderDetailBean.setId(orderDetailList.get(i).getId());
            orderDetailBean.setProductId(item.getId());
            orderDetailBean.setName(item.getProductName());
            orderDetailBean.setQuantity(orderDetailList.get(i).getQty());
            orderDetailBean.setPrice(String.format("%.2f", item.getPrice()));
            orderDetailBean.setProductImage(item.getProductImage());
            detailsBean.add(orderDetailBean);
        }
        return detailsBean;
    }

    /**
     * @param status filter status of order
     * @return BaseResponseBean of type String for the data
     */
    public BaseResponseBean<String> generateCsvReport(String status) {
        String path = "/generate-report";
        ArrayList<String> statuses = new ArrayList<>();
        statuses.add("ordered");
        statuses.add("sent");
        statuses.add("delivered");
        statuses.add("cancelled");
        statuses.add("returned");
        try {
            List<OrderHeader> allOrder;
            if (status.equals("all")) {
                allOrder = this.orderHeaderRepository.findAll(Sort.by("createdAt").descending());
            } else if (statuses.contains(status.toLowerCase())) {
                allOrder = this.orderHeaderRepository.findAllByStatus(status, Sort.by("createdAt").descending());
            } else {
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Not valid status", path);
            }
            StringWriter csvData = this.orderLogicService.createCsv(allOrder);
            this.emailService.sendEmailToAdmin("naufal.pramudya11@gmail.com",
                    "Order Report for Admin",
                    String.format("The attached csv file contains %s customer order data from the database", status),
                    csvData);
        } catch (Exception e) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), path);
        }
        BaseResponseBean<String> response = new BaseResponseBean<>();
        response.setStatus(HttpStatus.OK);
        response.setData("Check email");
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * @param orderNumber unique order number
     * @param orderCustomerInfo customer info such as id, username, name, phone, email, and address
     * @return BaseResponseBean of type CreatedOrderBean
     */
    public BaseResponseBean<CreatedOrderBean> updateOrderDelivered(String orderNumber, OrderCustomerInfo orderCustomerInfo) {
        String path = "/update/delivered";
        BaseResponseBean<CreatedOrderBean> response = new BaseResponseBean<>();

        long customerId = orderCustomerInfo.getId();

        Optional<OrderHeader> orderHeaderData = this.orderHeaderRepository.findByCustomerIdAndOrderNumber(customerId, orderNumber);
        if (orderHeaderData.isEmpty())
            throw new OrderCustomException(HttpStatus.NOT_FOUND, String.format("Order With order number: %s is not found", orderNumber), path);
        Optional<OrderDelivery> orderDeliveryData = this.orderDeliveryRepository.findByOrderHeader(orderHeaderData.get());

        OrderHeader orderHeader = orderHeaderData.get();
        OrderDelivery orderDelivery = orderDeliveryData.get();
        if (!orderHeader.getStatus().equals(Status.SENT))
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Can not deliver order with status: " + orderHeader.getStatus(), path);

        orderHeader.setStatus(Status.DELIVERED);
        orderHeader.setModifiedAt(LocalDateTime.now());
        orderHeader.setModifiedBy(orderHeader.getCreatedBy());
        orderDelivery.setDelivered(true);
        this.orderHeaderRepository.save(orderHeader);
        this.orderDeliveryRepository.save(orderDelivery);

        CreatedOrderBean createdOrderBean = new CreatedOrderBean();
        createdOrderBean.setStatus(Status.DELIVERED);
        createdOrderBean.setOrderNumber(orderNumber);
        createdOrderBean.setCreatedAt(orderHeader.getCreatedAt());
        createdOrderBean.setModifiedAt(LocalDateTime.now());
        createdOrderBean.setTotalPaid(String.format("%.2f", orderHeader.getTotalPaid()));

        response.setStatus(HttpStatus.OK);
        response.setData(createdOrderBean);
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());

        // fetch product info from catalog API for htmlContent data
        List<OrderDetail> orderDetailList = orderHeader.getOrderDetailList();
        List<Integer> temp = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            temp.add(Math.toIntExact(orderDetail.getProductId()));
        }
        OrderProductResponse orderProductResponse = this.orderLogicService.getAllProductInfo(temp);
        String htmlContent = this.orderLogicService.orderReportHtml(orderHeader, orderProductResponse);
        try {
            this.emailService.sendEmailToCustomer(orderCustomerInfo.getEmail(), "Order Report", htmlContent);
        } catch (MessagingException e) {
            throw new OrderCustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "/update/delivered");
        }
        return response;
    }
}
