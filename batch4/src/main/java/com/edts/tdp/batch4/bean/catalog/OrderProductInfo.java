package com.edts.tdp.batch4.bean.catalog;

import lombok.Data;

@Data
public class OrderProductInfo {
    private long id;
    private String productName;
    private String productImage;
    private double price;
    private int stock;
}
