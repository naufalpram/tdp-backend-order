package com.edts.tdp.batch4.bean.response;

import com.edts.tdp.batch4.bean.BaseResponseBean;

import java.time.LocalDateTime;

public class CreatedOrderBean extends BaseResponseBean {
    private LocalDateTime createdAt;
    private String orderNumber;
    private double totalPaid;
    private String status;

}
