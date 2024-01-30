package com.edts.tdp.batch4.repository;

import com.edts.tdp.batch4.bean.response.CreatedOrderBean;
import com.edts.tdp.batch4.model.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    @Query("SELECT new com.edts.tdp.batch4.bean.response.CreatedOrderBean" +
            "(oh.createdAt, oh.modifiedAt, oh.orderNumber, oh.totalPaid, oh.status) " +
            " FROM OrderHeader oh WHERE oh.customerId = :customerId")
    Page<CreatedOrderBean> findAllByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT new com.edts.tdp.batch4.bean.response.CreatedOrderBean" +
            "(oh.createdAt, oh.modifiedAt, oh.orderNumber, oh.totalPaid, oh.status) " +
            " FROM OrderHeader oh WHERE oh.customerId = :customerId AND oh.status = :status")
    Page<CreatedOrderBean> findAllByCustomerIdAndStatus(@Param("customerId") long customerId,
                                                        @Param("status") String status, Pageable pageable);
    Optional<OrderHeader> findByOrderNumber(String orderNumber);
    Optional<OrderHeader> findByCustomerIdAndOrderNumber(long customerId, String orderNumber);
}
