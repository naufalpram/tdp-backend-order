package com.edts.tdp.batch4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OrderHeaderTest {
    private OrderHeader orderHeader;
    //private OrderDetailList orderDetailList;
    //private OrderDelivery orderDelivery;
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

       createdAt = LocalDateTime.now();
       orderHeader.setCreatedAt(createdAt);

       modifiedAt = LocalDateTime.now();
       orderHeader.setModifiedAt(modifiedAt);
    }

    @Test //true test
    void testGetId(){assertEquals(1, orderHeader.getId());}

    @Test //false test
    void testGetWrongId(){assertNotEquals(1, orderHeader.getId());}

    @Test
    void testGetCreatedAt(){assertEquals(createdAt, orderHeader.getCreatedAt());}

    @Test //true test
    void testGetCreateBy(){assertEquals(1, orderHeader.getCreatedBy());}

    @Test //false test
    void testGetWrongCreateBy(){assertNotEquals(1, orderHeader.getCreatedBy());}

    @Test
    void testGetModifiedAt(){assertEquals(modifiedAt,orderHeader.getModifiedAt());}

    @Test//true
    void testGetCustomerId(){assertEquals(1, orderHeader.getCustomerId());}

    @Test //false
    void testGetWrongCustomerId(){assertNotEquals(1, orderHeader.getCustomerId());}

    //orderNumber
    @Test//true
    void testGetTotalPaid(){assertEquals(12500, orderHeader.getTotalPaid());}

    @Test//false
    void testGetWrongPaid(){assertNotEquals(12500, orderHeader.getTotalPaid());}

    @Test
    void testGetStatus(){assertEquals("open", orderHeader.getStatus());}

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
