package com.edts.tdp.batch4.repository;

import com.edts.tdp.batch4.model.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    Page<OrderHeader> findAllByCustomerId(Long customerId, Pageable pageable);
}
