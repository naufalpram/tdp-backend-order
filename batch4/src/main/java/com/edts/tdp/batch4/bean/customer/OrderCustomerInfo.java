package com.edts.tdp.batch4.bean.customer;

import lombok.Data;

@Data
public class OrderCustomerInfo {
    private long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private OrderCustomerAddress address;
}
