package com.edts.tdp.batch4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderHeaderTest {
    private OrderHeader orderHeader;
    private OrderDetail orderDetail;
    private OrderDelivery orderDelivery;
    private LocalDateTime createdAt, modifiedAt;

    @BeforeEach
    void setUp(){
       orderHeader = new OrderHeader();
       orderHeader.setId(1L);
       orderHeader.setCustomerId(2L);
       orderHeader.setCreatedBy(12L);
       orderHeader.setModifiedBy(1L);
       orderHeader.setOrderNumber("123l");
       orderHeader.setTotalPaid(12.500);
       orderHeader.setStatus("send");

       orderDetail = new OrderDetail();
       orderDetail.setId(1L);
       orderDetail.setCreatedBy("12 Januari 2023");
       orderDetail.setModifiedBy("13 Januari 2023");
       orderDetail.setProductId(12L);
       orderDetail.setQty(12);
       orderDetail.setPrice(123000);

       orderDelivery = new OrderDelivery();
       orderDelivery.setId(1L);
       orderDelivery.setCreatedBy("12 Januari 2023");


       createdAt = LocalDateTime.now();
       orderHeader.setCreatedAt(createdAt);

       modifiedAt = LocalDateTime.now();
       orderHeader.setModifiedAt(modifiedAt);
    }

    @Test //true test
    void testGetId(){assertEquals(1, orderHeader.getId());}

    @Test //false test
    void testGetWrongId(){assertNotEquals(2, orderHeader.getId());}

    @Test
    void testGetCreatedAt(){assertEquals(createdAt, orderHeader.getCreatedAt());}

    @Test //true test
    void testGetCreateBy(){assertEquals(12, orderHeader.getCreatedBy());}

    @Test //false test
    void testGetWrongCreateBy(){assertNotEquals(1, orderHeader.getCreatedBy());}

    @Test
    void testGetModifiedAt(){assertEquals(modifiedAt,orderHeader.getModifiedAt());}

    @Test//true
    void testGetCustomerId(){assertEquals(2, orderHeader.getCustomerId());}

    @Test //false
    void testGetWrongCustomerId(){assertNotEquals(1, orderHeader.getCustomerId());}

    //orderNumber
    @Test//true
    void testGetTotalPaid(){assertEquals(12.500, orderHeader.getTotalPaid());}

    @Test//false
    void testGetWrongPaid(){assertNotEquals(12500, orderHeader.getTotalPaid());}

    @Test
    void testGetStatus(){assertEquals("send", orderHeader.getStatus());}

    @Test
    void testGetWrongStatus() {
        assertNotEquals("Ordered", orderHeader.getStatus());
        assertNotEquals("Sent", orderHeader.getStatus());
        assertNotEquals("Delivered", orderHeader.getStatus());
        assertNotEquals("Cancelled", orderHeader.getStatus());
        assertNotEquals("Returned", orderHeader.getStatus());
    }

    //orderdelivery

}
