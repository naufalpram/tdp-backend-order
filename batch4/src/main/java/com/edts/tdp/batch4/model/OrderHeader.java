package com.edts.tdp.batch4.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


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

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @Column(name = "total_paid", nullable = false)
    private Double totalPaid;

}
