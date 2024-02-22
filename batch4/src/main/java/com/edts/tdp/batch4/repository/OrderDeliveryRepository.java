package com.edts.tdp.batch4.repository;

import com.edts.tdp.batch4.model.OrderDelivery;
import com.edts.tdp.batch4.model.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Integer> {
    Optional<OrderDelivery> findByOrderHeader(OrderHeader orderHeader);
}
