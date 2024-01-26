package com.edts.tdp.batch4.bean;

import lombok.Data;

@Data
public class RequestProductBean {
    private Long productId;
    private int qty;
    private double price;
}
