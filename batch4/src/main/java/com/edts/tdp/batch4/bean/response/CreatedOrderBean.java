package com.edts.tdp.batch4.bean.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CreatedOrderBean {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String orderNumber;
    private String totalPaid;
    private String status;

    public CreatedOrderBean(LocalDateTime createdAt, LocalDateTime modifiedAt, String orderNumber, Double totalPaid, String status) {
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.orderNumber = orderNumber;
        this.totalPaid = String.format("%.2f", totalPaid);
        this.status = status;
    }

    public CreatedOrderBean() {
    }
}
