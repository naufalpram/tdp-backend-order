package com.edts.tdp.batch4.bean.catalog;

import lombok.Data;

import java.util.List;

@Data
public class OrderProductResponse {
    private String status;
    private String message;
    private List<OrderProductInfo> data;
}
