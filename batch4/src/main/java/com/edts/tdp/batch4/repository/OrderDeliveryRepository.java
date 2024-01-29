package com.edts.tdp.batch4.repository;

import com.edts.tdp.batch4.model.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Integer> {

}
