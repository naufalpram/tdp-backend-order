package com.edts.tdp.batch4.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_detail")
@Data
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by")
    private String modifiedBy;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id")
    private OrderHeader orderHeader;

    @Column(name = "product_id")
    private Long productId;

    @Column
    private int qty;

    @Column
    private double price;

    public OrderDetail() {
        this.createdAt = LocalDateTime.now();
    }
}
