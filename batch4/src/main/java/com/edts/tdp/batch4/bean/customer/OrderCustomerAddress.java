package com.edts.tdp.batch4.bean.customer;

import lombok.Data;

@Data
public class OrderCustomerAddress {
    private String province;
    private String city;
    private String street;
    private String postcode;
    private double latitude;
    private double longitude;
}