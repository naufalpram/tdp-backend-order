//package com.edts.tdp.batch4.service;
//
//import com.edts.tdp.batch4.model.OrderDelivery;
//import com.edts.tdp.batch4.model.OrderHeader;
//import com.edts.tdp.batch4.utils.JwtUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//class OrderLogicServiceTest {
//    @Autowired
//    private OrderLogicService orderLogicService;
//
//    private JwtUtil jwtUtil;
//
//    private OrderHeader orderHeader;
//    private OrderDelivery orderDelivery;
//
//    @BeforeEach
//    public void init() {
//        orderHeader = new OrderHeader();
//        orderHeader.setId(1l);
//        orderHeader.setOrderNumber("123456789");
//        orderHeader.setTotalPaid(12.500);
//        orderHeader.setCreatedBy("John Doe");
//        orderHeader.setStatus("PENDING");
//
//        orderDelivery = new OrderDelivery();
//        orderDelivery.setStreet("Jl. Kebon Jeruk 123");
//        orderDelivery.setProvince("Jakarta Barat");
//        orderDelivery.setDistanceInKm(10);
//
//        JwtUtil jwtUtilMock = mock(JwtUtil.class);
//        orderLogicService = new OrderLogicService(jwtUtilMock);
//    }
//
//    @Test
//    public void createCsvTest() throws Exception {
//        List<OrderHeader> orders = new ArrayList<>();
//        orders.add(orderHeader);
//
//        StringWriter stringWriter = orderLogicService.createCsv(orders);
//        String csv = stringWriter.toString();
//
//        String[] expectedCsv = {
//                "Order Id,Order Number,Username,Alamat,Jarak,Total Pembayaran,Status",
//                "1,123456789,John Doe,Jl. Kebon Jeruk 123,10.00,10000.00,PENDING"
//        };
//
//        assertArrayEquals(expectedCsv, csv.split("\n"));
//    }
//    @Test
//    void testCheckOrderDelivery() {
//        // Arrange
//        OrderDelivery toCheck = new OrderDelivery();
//        toCheck.setStreet("Sample Street");
//        toCheck.setProvince("Sample Province");
//        toCheck.setDistanceInKm(10.5);
//
//        // Act
//        OrderDelivery checkedOrderDelivery = orderLogicService.checkOrderDelivery(toCheck);
//
//        // Assert
//        assertEquals("", checkedOrderDelivery.getStreet());
//        assertEquals("", checkedOrderDelivery.getProvince());
//        assertEquals(10.5, checkedOrderDelivery.getDistanceInKm());
//    }
//
//    //public OrderLogicService
//    //public double distanceCounter
//    //public double deliveryCost
//    //public OrderCustomerInfo getCustomerInfo
//    //public StringWriter createCsv
//
//    //public OrderProductResponse getAllProductInfo
//
//
////    @Test
////    void testOrderReportHtml() {
////        // Arrange
////        OrderLogicService orderLogicService = new OrderLogicService();
////
////        // Create sample OrderHeader
////        OrderHeader orderHeader = new OrderHeader();
////        orderHeader.setOrderNumber("ORD001");
////        orderHeader.setTotalPaid(100.0);
////        orderHeader.setModifiedAt(LocalDateTime.now()); // Assuming it's set to current time
////
////        // Create sample OrderProductResponse
////        List<OrderProductInfo> productInfoList = new ArrayList<>();
////        productInfoList.add(new OrderProductInfo(1L, "Product A", "", 10.0, 2));
////        productInfoList.add(new OrderProductInfo(2L, "Product B", "", 20.0, 3));
////        OrderProductResponse productDetails = new OrderProductResponse();
////        productDetails.setData(productInfoList);
////
////        // Act
////        String htmlContent = orderLogicService.orderReportHtml(orderHeader, productDetails);
////
////        // Assert
////        assertTrue(htmlContent.contains("<html>"));
////        assertTrue(htmlContent.contains("<body>"));
////        assertTrue(htmlContent.contains("<h1>Your Order Report</h1>"));
////        assertTrue(htmlContent.contains("<h4>Order Number: ORD001</h4>"));
////        assertTrue(htmlContent.contains("<h4>Total Paid: Rp100.00</h4>"));
////        assertTrue(htmlContent.contains("<ul>"));
////        assertTrue(htmlContent.contains("<li><p>Product A 2x | Rp20.00</p></li>"));
////        assertTrue(htmlContent.contains("<li><p>Product B 3x | Rp60.00</p></li>"));
////        assertTrue(htmlContent.contains("<h4>Delivered at:")); // Assuming date is present
////    }
//    //public boolean updateStockProduct
//    //public List<LinkedHashMap<String,Integer>> getCartData
//    //public boolean clearCartCustomer
//
//
////    @Test
////    void testGetCustomerAddress() {
////        // Arrange
////        OrderCustomerAddress expectedAddress = new OrderCustomerAddress();
////        expectedAddress.setCity("Oxford");
////        expectedAddress.setLatitude(10.0d);
////        expectedAddress.setLongitude(10.0d);
////        expectedAddress.setPostcode("OX1 1PT");
////        expectedAddress.setProvince("Province");
////        expectedAddress.setStreet("Street");
////
////        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
////        orderCustomerInfo.setAddress(expectedAddress);
////
////        // Create a mock for JwtUtil
////        JwtUtil jwtUtilMock = mock(JwtUtil.class);
////
////        // Mock the behavior of jwtUtilMock if necessary
////        // For example:
////        // when(jwtUtilMock.someMethod()).thenReturn(someValue);
////
////        OrderLogicService orderLogicService = new OrderLogicService(jwtUtilMock);
////
////        // Act
////        OrderCustomerAddress actualAddress = orderLogicService.getCustomerAddress(orderCustomerInfo);
////
////        // Assert
////        assertEquals(expectedAddress, actualAddress);
////    }
//
////    @Test
////    void testCreateCsv() {
////        // Arrange
////        List<OrderHeader> orders = new ArrayList<>();
////        // Add some sample OrderHeader objects to the list
////
////        // Create OrderLogicService instance
////        OrderLogicService orderLogicService = new OrderLogicService();
////
////        // Act
////        StringWriter stringWriter = orderLogicService.createCsv(orders);
////
////        // Assert
////        // Perform assertions on the content of the generated CSV
////        String expectedCsvContent = "Expected CSV content"; // Replace with expected CSV content
////        assertEquals(expectedCsvContent, stringWriter.toString());
////    }
//
//
//    /**
//     * Method under test:
//     * {@link OrderLogicService#clearCartCustomer(HttpServletRequest, String)}
//     */
//    @Test
//    @Disabled("TODO: Complete this test")
//    void testClearCartCustomer() {
//        // Arrange and Act
//        orderLogicService.clearCartCustomer(new MockHttpServletRequest(), "Path");
//    }
//}
