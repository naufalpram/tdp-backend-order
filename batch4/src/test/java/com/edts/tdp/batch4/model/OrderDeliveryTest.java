package com.edts.tdp.batch4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OrderDeliveryTest {
    private OrderDelivery orderDelivery;
    private OrderHeader orderHeader;
    private LocalDateTime createdAt, modifiedAt;

    @BeforeEach
    void setUpOrderDeliveryTest() {
        orderDelivery = new OrderDelivery();
        orderDelivery.setId(1L);
        orderDelivery.setCreatedBy("Amini");
        orderDelivery.setModifiedBy("Amini");
        orderDelivery.setStreet("Sudirman");
        orderDelivery.setProvince("Jakarta");
        orderDelivery.setDistanceInKm(3.2);
        orderDelivery.setLatitude(-6.175205678775132);
        orderDelivery.setLongitude(106.82715894445303);

        orderHeader = new OrderHeader();
        orderHeader.setId(1L);
        orderHeader.setCustomerId(2L);
        orderHeader.setCreatedBy("Amini");
        orderHeader.setModifiedBy("Amini");
        orderHeader.setOrderNumber("123l");
        orderHeader.setTotalPaid(12.500);
        orderHeader.setStatus("send");

        createdAt = LocalDateTime.now();
        orderDelivery.setCreatedAt(createdAt);
        modifiedAt = LocalDateTime.now();
        orderDelivery.setModifiedAt(modifiedAt);
        orderDelivery.setOrderHeader(orderHeader);
    }
    @Test//true
    void testGetId() {assertEquals(1, orderDelivery.getId());}

    @Test//false
    void testGetWrongId() {assertNotEquals(2, orderDelivery.getId());}

    @Test
    void testGetCreateAt() {assertEquals(createdAt, orderDelivery.getCreatedAt());}

    @Test //true
    void testGetCreateBy() {assertEquals("Amini", orderDelivery.getCreatedBy());}

    @Test//false
    void testGetWrongCreateBy() {assertNotEquals("amini", orderDelivery.getCreatedBy());}

    @Test
    void testGetModifiedAt() {assertEquals(modifiedAt, orderDelivery.getModifiedAt());}

    @Test//true
    void testGetModifiedBy() {assertEquals("Amini", orderDelivery.getModifiedBy());}

    @Test//false
    void testGetWrongModifiedBy() {assertNotEquals("amini", orderDelivery.getModifiedBy());}

    @Test//true
    void testGetStreet() {assertEquals("Sudirman", orderDelivery.getStreet());}

    @Test//false
    void testGetWrongStreet() {assertNotEquals("sudirman", orderDelivery.getStreet());}

    @Test//true
    void testGetProvince() {assertEquals("Jakarta", orderDelivery.getProvince());}

    @Test//false
    void testGetWrongProvince() {assertNotEquals("jakarta", orderDelivery.getProvince());}

    @Test//true
    void testGetDistanceInKm() {assertEquals(3.2, orderDelivery.getDistanceInKm());}

    @Test//false
    void testGetWrongDistanceInKm() {assertNotEquals(3., orderDelivery.getDistanceInKm());}

    @Test//true
    void testGetLatitude() {assertEquals(-6.175205678775132, orderDelivery.getLatitude());}

    @Test//false
    void testGetWrongLatitude() {assertNotEquals(6.175205678775132, orderDelivery.getLatitude());}

    @Test//true
    void testGetLongitude() {assertEquals(106.82715894445303, orderDelivery.getLongitude());}

    @Test//false
    void testGetWrongLongitude() {assertNotEquals(-106.82715894445303, orderDelivery.getLongitude());}


    @Test//true
    void testGetOrderHeader() {
        assertEquals(this.orderHeader, orderDelivery.getOrderHeader());
    }

    @Test//false
    void testGetWrongOrderHeader() {
        OrderHeader orderHeaderTest = new OrderHeader();
        assertNotEquals(orderHeaderTest, orderDelivery.getOrderHeader());
    }

}
