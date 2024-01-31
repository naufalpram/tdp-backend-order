package com.edts.tdp.batch4.bean.response;

import lombok.Data;

@Data
public class OrderDetailBean {
    private Long id;
    private Long productId;
    private int qty;
    private String price;
    private String productImage;
    // tambahin name
}
