package com.edts.tdp.batch4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OrderDetailTest {
    private OrderDetail orderDetail;
    private OrderHeader orderHeader;

    private LocalDateTime createdAt, modifiedAt;
    private List<OrderDetail> itemList;

    @BeforeEach
    void setUpOrderDetailTest() {
        orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setCreatedBy("Amini");
        orderDetail.setModifiedBy("Amini");
        orderDetail.setProductId(12L);
        orderDetail.setQty(12);
        orderDetail.setPrice(12.500);

        orderHeader = new OrderHeader();
        orderHeader.setId(1L);
        orderHeader.setCustomerId(2L);
        orderHeader.setCreatedBy("Amini");
        orderHeader.setModifiedBy("Amini");
        orderHeader.setOrderNumber("123l");
        orderHeader.setTotalPaid(12.500);
        orderHeader.setStatus("send");

        createdAt = LocalDateTime.now();
        orderDetail.setCreatedAt(createdAt);
        modifiedAt = LocalDateTime.now();
        orderDetail.setModifiedAt(modifiedAt);

        orderDetail.setOrderHeader(orderHeader);
        orderHeader.setCreatedAt(createdAt);
        modifiedAt = LocalDateTime.now();
        orderHeader.setModifiedAt(modifiedAt);

        itemList = new ArrayList<>();
        itemList.add(orderDetail);

        orderDetail.setOrderHeader(orderHeader);
    }

    @Test//true
    void testGetId() {assertEquals(1, orderDetail.getId());}

    @Test//false
    void testGetWrongId() {assertNotEquals(2, orderDetail.getId());}

    @Test
    void testGetCreateAt() {assertEquals(createdAt, orderDetail.getCreatedAt());}

    @Test //true
    void testGetCreateBy() {assertEquals("Amini", orderDetail.getCreatedBy());}

    @Test//false
    void testGetWrongCreateBy() {assertNotEquals("amini", orderDetail.getCreatedBy());}

    @Test
    void testGetModifiedAt() {assertEquals(modifiedAt, orderDetail.getModifiedAt());}

    @Test//true
    void testGetModifiedBy() {assertEquals("Amini", orderDetail.getModifiedBy());}

    @Test//false
    void testGetWrongModifiedBy() {assertNotEquals("amini", orderDetail.getModifiedBy());}

    @Test//true
    void testGetProductid() {assertEquals(12,orderDetail.getProductId());}

    @Test//false
    void testGetWrongProductId() {assertNotEquals(2, orderDetail.getProductId());}

    @Test//true
    void testGetQty() {assertEquals(12,orderDetail.getQty());}

    @Test//false
    void testGetWrongGetQty() {assertNotEquals(2, orderDetail.getQty());}

    @Test//true
    void testGetPrice() {assertEquals(12.500,orderDetail.getPrice());}

    @Test//false
    void testGetWrongGetPrice() {assertNotEquals(2, orderDetail.getPrice());}

    @Test//true
    void testGetOrderHeader() {
        assertEquals(this.orderHeader, orderDetail.getOrderHeader());
    }

    @Test//false
    void testGetWrongOrderHeader() {
        OrderHeader orderHeaderTest = new OrderHeader();
        assertNotEquals(orderHeaderTest, orderDetail.getOrderHeader());
    }
}
