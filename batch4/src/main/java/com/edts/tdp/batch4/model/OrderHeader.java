package com.edts.tdp.batch4.model;

import com.edts.tdp.batch4.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "order_header")
@Data
public class OrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, length = 20)
    private String createdBy;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by", length = 20)
    private String modifiedBy;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "order_number", unique = true, length = 24)
    private String orderNumber;

    @Column(name = "total_paid", nullable = false)
    private Double totalPaid;

    @Column(name = "status", nullable = false)
    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "orderHeader")
    private List<OrderDetail> orderDetailList;

    @OneToOne(mappedBy = "orderHeader")
    @JsonIgnoreProperties({"orderHeader"})
    private OrderDelivery orderDelivery;

    public OrderHeader() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.ORDERED;
        this.orderNumber = UUID.randomUUID().toString();
    }

}
