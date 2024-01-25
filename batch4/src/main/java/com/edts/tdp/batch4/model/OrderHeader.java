package com.edts.tdp.batch4.model;

import com.edts.tdp.batch4.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


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

    @Column(name = "status", nullable = false)
    private String status;

    /*
     *@JsonManagedReference digunakan untuk mereference  hubungan
     *@OneToMany Hubungan ini menggambarkan relasi tabel dari order_header dengan orderDetail
     *mappedBy digunakan untuk menjelaskan tabel mana yang akan dihubungkan
     *list digunakan karena header bisa punya banyak produk, sedangkan list hanya punya saru makanya masuk ke orderDetail
     *private OrderDelivery orderDelivery; ngreference ke classnya
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "order_header")
    private List<OrderDetail> orderDetailList;

    /*
    *@OneToOne Hubungan ini menggambarkan relasi tabel dari order_header dengan orderDelivery
    *mappedBy digunakan untuk menjelaskan tabel mana yang akan dihubungkan
    *@JsonIgnoreProperties ini untuk mencegah pembacaan berulang dari relasi
    *private OrderDelivery orderDelivery; ngreference ke classnya
    */
    @OneToOne(mappedBy = "order_header")
    @JsonIgnoreProperties({"order_header"})
    private OrderDelivery orderDelivery;

    public OrderHeader() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.ORDERED;
    }

}
