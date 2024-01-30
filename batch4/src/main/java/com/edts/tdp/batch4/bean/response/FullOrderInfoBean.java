package com.edts.tdp.batch4.bean.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FullOrderInfoBean {
    private Long id;
    private LocalDateTime createdAt;
    private Long customerId;
    private String orderNumber;
    private String totalPaid;
    private String status;
    private String street;
    private String province;
    private String postalCode;
    private double distanceInKm;
    private List<OrderDetailBean> orderDetailList;
}
