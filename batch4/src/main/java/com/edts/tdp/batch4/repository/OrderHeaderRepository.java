package com.edts.tdp.batch4.repository;

import com.edts.tdp.batch4.model.OrderHeader;

import org.hibernate.query.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    Page<OrderHeader> findAllByCustomerId(Long customerId, Pageable pageable);
    Page<OrderHeader> findAllByCustomerIdAndStatus(long customerId, String status, Pageable pageable);
}
