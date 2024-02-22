package com.edts.tdp.batch4.bean.response;

import lombok.Data;

@Data
public class OrderDetailBean {
    private Long id;
    private Long productId;
    private String name;
    private int quantity;
    private String price;
    private String productImage;
}
