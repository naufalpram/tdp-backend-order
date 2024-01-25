package com.edts.tdp.batch4.constant;

import org.springframework.stereotype.Component;

@Component
public class Status {

    public static final String ORDERED = "Ordered";
    public static final String SENT = "Sent";
    public static final String DELIVERED = "Delivered";
    public static final String CANCELLED = "Cancelled";
    public static final String RETURNED = "Returned";
}
