package com.edts.tdp.batch4.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.edts.tdp.batch4.config.RsaKeyProperties;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import com.edts.tdp.batch4.service.EmailService;
import com.edts.tdp.batch4.service.OrderLogicService;
import com.edts.tdp.batch4.service.OrderService;
import com.edts.tdp.batch4.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {OrderController.class, OrderLogicService.class, OrderService.class, EmailService.class,
        JwtUtil.class, RsaKeyProperties.class})
@ExtendWith(SpringExtension.class)
class OrderControllerTest {
    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private OrderController orderController;

    @MockBean
    private OrderDeliveryRepository orderDeliveryRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    @MockBean
    private OrderHeaderRepository orderHeaderRepository;

    @MockBean
    private RSAPrivateKey rSAPrivateKey;

    @MockBean
    private RSAPublicKey rSAPublicKey;

    /**
     * Method under test:
     * {@link OrderController#getAllOrdersByStatus(Map, int, int, HttpServletRequest)}
     */
    @Test
    void testGetAllOrdersByStatus() {
        // Arrange
        OrderController orderController = new OrderController();
        HashMap<String, String> request = new HashMap<>();

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderController.getAllOrdersByStatus(request, 1, 3, new MockHttpServletRequest()));
    }

    /**
     * Method under test:
     * {@link OrderController#cancelOrder(Map, HttpServletRequest)}
     */
    @Test
    void testCancelOrder() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/api/v1/order/update/cancel");
        postResult.characterEncoding("https://example.org/example");

        HashMap<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put((String) "orderNumber", "foo");
        stringStringMap.put((String) "orderNumber", "foo");
        String content = (new ObjectMapper()).writeValueAsString(stringStringMap);
        MockHttpServletRequestBuilder requestBuilder = postResult.contentType(MediaType.APPLICATION_JSON).content(content);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(orderController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(415));
    }

    /**
     * Method under test: {@link OrderController#createOrder(HttpServletRequest)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCreateOrder() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/order/create");

        // Act
        MockMvcBuilders.standaloneSetup(orderController).build().perform(requestBuilder);
    }

    /**
     * Method under test: {@link OrderController#generateOrderReport(String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGenerateOrderReport() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/order/generate-report/{status}",
                "Status");

        // Act
        MockMvcBuilders.standaloneSetup(orderController).build().perform(requestBuilder);
    }

    /**
     * Method under test:
     * {@link OrderController#getAllOrders(int, int, HttpServletRequest)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetAllOrders() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/api/v1/order/get-history");
        MockHttpServletRequestBuilder paramResult = getResult.param("page", String.valueOf(1));
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("size", String.valueOf(1));

        // Act
        MockMvcBuilders.standaloneSetup(orderController).build().perform(requestBuilder);
    }

    /**
     * Method under test:
     * {@link OrderController#updateOrderSent(Map, HttpServletRequest)}
     */
    @Test
    void testUpdateOrderSent() {
        // Arrange
        OrderController orderController = new OrderController();
        HashMap<String, String> request = new HashMap<>();

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderController.updateOrderSent(request, new MockHttpServletRequest()));
    }

    /**
     * Method under test:
     * {@link OrderController#returnOrder(Map, HttpServletRequest)}
     */
    @Test
    void testReturnOrder() {
        // Arrange
        OrderController orderController = new OrderController();
        HashMap<String, String> request = new HashMap<>();

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderController.returnOrder(request, new MockHttpServletRequest()));
    }

    /**
     * Method under test:
     * {@link OrderController#getFullOrderInfo(Map, HttpServletRequest)}
     */
    @Test
    void testGetFullOrderInfo() {
        // Arrange
        OrderController orderController = new OrderController();
        HashMap<String, String> request = new HashMap<>();

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderController.getFullOrderInfo(request, new MockHttpServletRequest()));
    }

    /**
     * Method under test:
     * {@link OrderController#updateOrderDelivered(Map, HttpServletRequest)}
     */
    @Test
    void testUpdateOrderDelivered() {
        // Arrange
        OrderController orderController = new OrderController();
        HashMap<String, String> request = new HashMap<>();

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderController.updateOrderDelivered(request, new MockHttpServletRequest()));
    }
}
