package com.edts.tdp.batch4.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "order_delivery")
public class OrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column private long id;
<<<<<<< HEAD
    @Column(name = "created_at", columnDefinition = "TIMESTAMP") private LocalDateTime createdAt;
    @Column(name = "created_by", nullable = false) private String createdBy;
    @Column(name = "modified_at", columnDefinition = "TIMESTAMP") private LocalDateTime modifiedAt;
    @Column(name = "modified_by") private String modifiedBy;
=======
    
    @Column(name = "created_at", columnDefinition = "TIMESTAMP") 
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", nullable = false, length = 20) 
    private String createdBy;
    
    @Column(name = "modified_at", columnDefinition = "TIMESTAMP") 
    private LocalDateTime modifiedAt;
    
    @Column(name = "modified_by", length = 20) 
    private String modifiedBy;
    
>>>>>>> f9a607159fb265e959d55743078629a7f7bfbc4b
    @OneToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"orderDelivery"})
    private OrderHeader orderHeader;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String province;

    @Column(name = "distance_in_km", nullable = false)
    private double distanceInKm;

    @Column(name = "is_delivered", nullable = false)
    private boolean isDelivered;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    public OrderDelivery() {
        this.createdAt = LocalDateTime.now();
    }
}
