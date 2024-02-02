package com.edts.tdp.batch4.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import com.edts.tdp.batch4.bean.catalog.OrderProductResponse;
import com.edts.tdp.batch4.bean.customer.OrderCustomerAddress;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.edts.tdp.batch4.bean.response.FullOrderInfoBean;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderDelivery;
import com.edts.tdp.batch4.model.OrderDetail;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.repository.OrderDeliveryRepository;
import com.edts.tdp.batch4.repository.OrderDetailRepository;
import com.edts.tdp.batch4.repository.OrderHeaderRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {OrderService.class})
@ExtendWith(SpringExtension.class)
class OrderServiceTest {
    @MockBean
    private EmailService emailService;

    @MockBean
    private OrderDeliveryRepository orderDeliveryRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    @MockBean
    private OrderHeaderRepository orderHeaderRepository;

    @MockBean
    private OrderLogicService orderLogicService;

    @Autowired
    private OrderService orderService;

    /**
     * Method under test:
     * {@link OrderService#createOrder(OrderCustomerInfo, HttpServletRequest)}
     */
    @Test
    void testCreateOrder() {
        // Arrange
        when(orderLogicService.getCartData(Mockito.<HttpServletRequest>any(), Mockito.<String>any()))
                .thenReturn(new ArrayList<>());

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderService.createOrder(orderCustomerInfo, new MockHttpServletRequest()));
        verify(orderLogicService).getCartData(Mockito.<HttpServletRequest>any(), Mockito.<String>any());
    }

    /**
     * Method under test:
     * {@link OrderService#createOrder(OrderCustomerInfo, HttpServletRequest)}
     */

    /**
     * Method under test:
     * {@link OrderService#getAllOrderByCustomerId(int, int, OrderCustomerInfo)}
     */
    @Test
    void testGetAllOrderByCustomerId() {
        // Arrange
        when(orderHeaderRepository.findAllByCustomerId(Mockito.<Long>any(), Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderService.getAllOrderByCustomerId(1, 3, orderCustomerInfo));
        verify(orderHeaderRepository).findAllByCustomerId(Mockito.<Long>any(), Mockito.<Pageable>any());
    }

    /**
     * Method under test:
     * {@link OrderService#findAllByCustomerIdAndStatus(String, int, int, OrderCustomerInfo)}
     */
    @Test
    void testFindAllByCustomerIdAndStatus() {
        // Arrange
        when(orderHeaderRepository.findAllByCustomerIdAndStatus(anyLong(), Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class,
                () -> orderService.findAllByCustomerIdAndStatus("Status", 1, 3, orderCustomerInfo));
        verify(orderHeaderRepository).findAllByCustomerIdAndStatus(anyLong(), Mockito.<String>any(),
                Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link OrderService#sendOrder(String, OrderCustomerInfo)}
     */
    @Test
    void testSendOrder() {
        // Arrange
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery.setDelivered(true);
        orderDelivery.setDistanceInKm(10.0d);
        orderDelivery.setId(1L);
        orderDelivery.setLatitude(10.0d);
        orderDelivery.setLongitude(10.0d);
        orderDelivery.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery.setOrderHeader(new OrderHeader());
        orderDelivery.setPostCode("OX1 1PT");
        orderDelivery.setProvince("Province");
        orderDelivery.setStreet("Street");

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader.setCustomerId(1L);
        orderHeader.setId(1L);
        orderHeader.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader.setOrderDelivery(orderDelivery);
        orderHeader.setOrderDetailList(new ArrayList<>());
        orderHeader.setOrderNumber("42");
        orderHeader.setStatus("Status");
        orderHeader.setTotalPaid(10.0d);

        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery2.setDelivered(true);
        orderDelivery2.setDistanceInKm(10.0d);
        orderDelivery2.setId(1L);
        orderDelivery2.setLatitude(10.0d);
        orderDelivery2.setLongitude(10.0d);
        orderDelivery2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery2.setOrderHeader(orderHeader);
        orderDelivery2.setPostCode("OX1 1PT");
        orderDelivery2.setProvince("Province");
        orderDelivery2.setStreet("Street");

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader2.setCustomerId(1L);
        orderHeader2.setId(1L);
        orderHeader2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader2.setOrderDelivery(orderDelivery2);
        orderHeader2.setOrderDetailList(new ArrayList<>());
        orderHeader2.setOrderNumber("42");
        orderHeader2.setStatus("Status");
        orderHeader2.setTotalPaid(10.0d);
        Optional<OrderHeader> ofResult = Optional.of(orderHeader2);
        when(orderHeaderRepository.findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any())).thenReturn(ofResult);

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderService.sendOrder("42", orderCustomerInfo));
        verify(orderHeaderRepository).findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any());
    }


    /**
     * Method under test:
     * {@link OrderService#cancelOrder(String, OrderCustomerInfo)}
     */
    @Test
    void testCancelOrder() {
        // Arrange
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery.setDelivered(true);
        orderDelivery.setDistanceInKm(10.0d);
        orderDelivery.setId(1L);
        orderDelivery.setLatitude(10.0d);
        orderDelivery.setLongitude(10.0d);
        orderDelivery.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery.setOrderHeader(new OrderHeader());
        orderDelivery.setPostCode("OX1 1PT");
        orderDelivery.setProvince("Province");
        orderDelivery.setStreet("Street");

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader.setCustomerId(1L);
        orderHeader.setId(1L);
        orderHeader.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader.setOrderDelivery(orderDelivery);
        orderHeader.setOrderDetailList(new ArrayList<>());
        orderHeader.setOrderNumber("42");
        orderHeader.setStatus("Status");
        orderHeader.setTotalPaid(10.0d);

        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery2.setDelivered(true);
        orderDelivery2.setDistanceInKm(10.0d);
        orderDelivery2.setId(1L);
        orderDelivery2.setLatitude(10.0d);
        orderDelivery2.setLongitude(10.0d);
        orderDelivery2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery2.setOrderHeader(orderHeader);
        orderDelivery2.setPostCode("OX1 1PT");
        orderDelivery2.setProvince("Province");
        orderDelivery2.setStreet("Street");

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader2.setCustomerId(1L);
        orderHeader2.setId(1L);
        orderHeader2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader2.setOrderDelivery(orderDelivery2);
        orderHeader2.setOrderDetailList(new ArrayList<>());
        orderHeader2.setOrderNumber("42");
        orderHeader2.setStatus("Status");
        orderHeader2.setTotalPaid(10.0d);
        Optional<OrderHeader> ofResult = Optional.of(orderHeader2);
        when(orderHeaderRepository.findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any())).thenReturn(ofResult);

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderService.cancelOrder("42", orderCustomerInfo));
        verify(orderHeaderRepository).findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any());
    }

    /**
     * Method under test:
     * {@link OrderService#returnOrder(String, OrderCustomerInfo)}
     */
    @Test
    void testReturnOrder() {
        // Arrange
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery.setDelivered(true);
        orderDelivery.setDistanceInKm(10.0d);
        orderDelivery.setId(1L);
        orderDelivery.setLatitude(10.0d);
        orderDelivery.setLongitude(10.0d);
        orderDelivery.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery.setOrderHeader(new OrderHeader());
        orderDelivery.setPostCode("OX1 1PT");
        orderDelivery.setProvince("Province");
        orderDelivery.setStreet("Street");

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader.setCustomerId(1L);
        orderHeader.setId(1L);
        orderHeader.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader.setOrderDelivery(orderDelivery);
        orderHeader.setOrderDetailList(new ArrayList<>());
        orderHeader.setOrderNumber("42");
        orderHeader.setStatus("Status");
        orderHeader.setTotalPaid(10.0d);

        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery2.setDelivered(true);
        orderDelivery2.setDistanceInKm(10.0d);
        orderDelivery2.setId(1L);
        orderDelivery2.setLatitude(10.0d);
        orderDelivery2.setLongitude(10.0d);
        orderDelivery2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery2.setOrderHeader(orderHeader);
        orderDelivery2.setPostCode("OX1 1PT");
        orderDelivery2.setProvince("Province");
        orderDelivery2.setStreet("Street");

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader2.setCustomerId(1L);
        orderHeader2.setId(1L);
        orderHeader2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader2.setOrderDelivery(orderDelivery2);
        orderHeader2.setOrderDetailList(new ArrayList<>());
        orderHeader2.setOrderNumber("42");
        orderHeader2.setStatus("Status");
        orderHeader2.setTotalPaid(10.0d);
        Optional<OrderHeader> ofResult = Optional.of(orderHeader2);
        when(orderHeaderRepository.findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any())).thenReturn(ofResult);

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderService.returnOrder("42", orderCustomerInfo));
        verify(orderHeaderRepository).findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any());
    }

    /**
     * Method under test:
     * {@link OrderService#getFullOrderInfo(String, OrderCustomerInfo)}
     */
    @Test
    void testGetFullOrderInfo() {
        // Arrange
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery.setDelivered(true);
        orderDelivery.setDistanceInKm(10.0d);
        orderDelivery.setId(1L);
        orderDelivery.setLatitude(10.0d);
        orderDelivery.setLongitude(10.0d);
        orderDelivery.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery.setOrderHeader(new OrderHeader());
        orderDelivery.setPostCode("OX1 1PT");
        orderDelivery.setProvince("Province");
        orderDelivery.setStreet("Street");

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader.setCustomerId(1L);
        orderHeader.setId(1L);
        orderHeader.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader.setOrderDelivery(orderDelivery);
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        orderHeader.setOrderDetailList(orderDetailList);
        orderHeader.setOrderNumber("42");
        orderHeader.setStatus("Status");
        orderHeader.setTotalPaid(10.0d);

        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery2.setDelivered(true);
        orderDelivery2.setDistanceInKm(10.0d);
        orderDelivery2.setId(1L);
        orderDelivery2.setLatitude(10.0d);
        orderDelivery2.setLongitude(10.0d);
        orderDelivery2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery2.setOrderHeader(orderHeader);
        orderDelivery2.setPostCode("OX1 1PT");
        orderDelivery2.setProvince("Province");
        orderDelivery2.setStreet("Street");

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader2.setCustomerId(1L);
        orderHeader2.setId(1L);
        orderHeader2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader2.setOrderDelivery(orderDelivery2);
        orderHeader2.setOrderDetailList(new ArrayList<>());
        orderHeader2.setOrderNumber("42");
        orderHeader2.setStatus("Status");
        orderHeader2.setTotalPaid(10.0d);
        Optional<OrderHeader> ofResult = Optional.of(orderHeader2);
        when(orderHeaderRepository.findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any())).thenReturn(ofResult);

        OrderProductResponse orderProductResponse = new OrderProductResponse();
        orderProductResponse.setData(new ArrayList<>());
        orderProductResponse.setMessage("Not all who wander are lost");
        orderProductResponse.setStatus("Status");
        when(orderLogicService.getAllProductInfo(Mockito.<List<Integer>>any())).thenReturn(orderProductResponse);

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act
        BaseResponseBean<FullOrderInfoBean> actualFullOrderInfo = orderService.getFullOrderInfo("42", orderCustomerInfo);

        // Assert
        verify(orderHeaderRepository).findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any());
        verify(orderLogicService).getAllProductInfo(Mockito.<List<Integer>>any());
        FullOrderInfoBean data = actualFullOrderInfo.getData();
        assertEquals("00:00", data.getCreatedAt().toLocalTime().toString());
        assertEquals("10.00", data.getTotalPaid());
        assertEquals("42", data.getOrderNumber());
        assertEquals("OK", actualFullOrderInfo.getMessage());
        assertEquals("OX1 1PT", data.getPostalCode());
        assertEquals("Province", data.getProvince());
        assertEquals("Status", data.getStatus());
        assertEquals("Street", data.getStreet());
        assertEquals(10.0d, data.getDistanceInKm());
        assertEquals(1L, data.getCustomerId().longValue());
        assertEquals(1L, data.getId().longValue());
        assertEquals(200, actualFullOrderInfo.getCode());
        assertEquals(HttpStatus.OK, actualFullOrderInfo.getStatus());
        assertEquals(orderDetailList, data.getOrderDetailList());
    }

    /**
     * Method under test: {@link OrderService#generateCsvReport(String)}
     */
    @Test
    void testGenerateCsvReport() throws MessagingException {
        // Arrange
        when(orderHeaderRepository.findAllByStatus(Mockito.<String>any(), Mockito.<Sort>any()))
                .thenReturn(new ArrayList<>());
        doNothing().when(emailService)
                .sendEmailToAdmin(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
                        Mockito.<StringWriter>any());
        when(orderLogicService.createCsv(Mockito.<List<OrderHeader>>any())).thenReturn(new StringWriter());

        // Act
        BaseResponseBean<String> actualGenerateCsvReportResult = orderService.generateCsvReport("Status");

        // Assert
        verify(orderHeaderRepository).findAllByStatus(Mockito.<String>any(), Mockito.<Sort>any());
        verify(emailService).sendEmailToAdmin(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
                Mockito.<StringWriter>any());
        verify(orderLogicService).createCsv(Mockito.<List<OrderHeader>>any());
        assertEquals("Check email", actualGenerateCsvReportResult.getData());
        assertEquals("OK", actualGenerateCsvReportResult.getMessage());
        assertEquals(200, actualGenerateCsvReportResult.getCode());
        assertEquals(HttpStatus.OK, actualGenerateCsvReportResult.getStatus());
    }

    /**
     * Method under test:
     * {@link OrderService#updateOrderDelivered(String, OrderCustomerInfo)}
     */
    @Test
    void testUpdateOrderDelivered() {
        // Arrange
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery.setDelivered(true);
        orderDelivery.setDistanceInKm(10.0d);
        orderDelivery.setId(1L);
        orderDelivery.setLatitude(10.0d);
        orderDelivery.setLongitude(10.0d);
        orderDelivery.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery.setOrderHeader(new OrderHeader());
        orderDelivery.setPostCode("OX1 1PT");
        orderDelivery.setProvince("Province");
        orderDelivery.setStreet("Street");

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader.setCustomerId(1L);
        orderHeader.setId(1L);
        orderHeader.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader.setOrderDelivery(orderDelivery);
        orderHeader.setOrderDetailList(new ArrayList<>());
        orderHeader.setOrderNumber("42");
        orderHeader.setStatus("Status");
        orderHeader.setTotalPaid(10.0d);

        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery2.setDelivered(true);
        orderDelivery2.setDistanceInKm(10.0d);
        orderDelivery2.setId(1L);
        orderDelivery2.setLatitude(10.0d);
        orderDelivery2.setLongitude(10.0d);
        orderDelivery2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery2.setOrderHeader(orderHeader);
        orderDelivery2.setPostCode("OX1 1PT");
        orderDelivery2.setProvince("Province");
        orderDelivery2.setStreet("Street");

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader2.setCustomerId(1L);
        orderHeader2.setId(1L);
        orderHeader2.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader2.setOrderDelivery(orderDelivery2);
        orderHeader2.setOrderDetailList(new ArrayList<>());
        orderHeader2.setOrderNumber("42");
        orderHeader2.setStatus("Status");
        orderHeader2.setTotalPaid(10.0d);
        Optional<OrderHeader> ofResult = Optional.of(orderHeader2);
        when(orderHeaderRepository.findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any())).thenReturn(ofResult);

        OrderHeader orderHeader3 = new OrderHeader();
        orderHeader3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader3.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader3.setCustomerId(1L);
        orderHeader3.setId(1L);
        orderHeader3.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader3.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader3.setOrderDelivery(new OrderDelivery());
        orderHeader3.setOrderDetailList(new ArrayList<>());
        orderHeader3.setOrderNumber("42");
        orderHeader3.setStatus("Status");
        orderHeader3.setTotalPaid(10.0d);

        OrderDelivery orderDelivery3 = new OrderDelivery();
        orderDelivery3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery3.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery3.setDelivered(true);
        orderDelivery3.setDistanceInKm(10.0d);
        orderDelivery3.setId(1L);
        orderDelivery3.setLatitude(10.0d);
        orderDelivery3.setLongitude(10.0d);
        orderDelivery3.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery3.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery3.setOrderHeader(orderHeader3);
        orderDelivery3.setPostCode("OX1 1PT");
        orderDelivery3.setProvince("Province");
        orderDelivery3.setStreet("Street");

        OrderHeader orderHeader4 = new OrderHeader();
        orderHeader4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader4.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderHeader4.setCustomerId(1L);
        orderHeader4.setId(1L);
        orderHeader4.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderHeader4.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderHeader4.setOrderDelivery(orderDelivery3);
        orderHeader4.setOrderDetailList(new ArrayList<>());
        orderHeader4.setOrderNumber("42");
        orderHeader4.setStatus("Status");
        orderHeader4.setTotalPaid(10.0d);

        OrderDelivery orderDelivery4 = new OrderDelivery();
        orderDelivery4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery4.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        orderDelivery4.setDelivered(true);
        orderDelivery4.setDistanceInKm(10.0d);
        orderDelivery4.setId(1L);
        orderDelivery4.setLatitude(10.0d);
        orderDelivery4.setLongitude(10.0d);
        orderDelivery4.setModifiedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        orderDelivery4.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        orderDelivery4.setOrderHeader(orderHeader4);
        orderDelivery4.setPostCode("OX1 1PT");
        orderDelivery4.setProvince("Province");
        orderDelivery4.setStreet("Street");
        Optional<OrderDelivery> ofResult2 = Optional.of(orderDelivery4);
        when(orderDeliveryRepository.findByOrderHeader(Mockito.<OrderHeader>any())).thenReturn(ofResult2);

        OrderCustomerAddress address = new OrderCustomerAddress();
        address.setCity("Oxford");
        address.setLatitude(10.0d);
        address.setLongitude(10.0d);
        address.setPostcode("OX1 1PT");
        address.setProvince("Province");
        address.setStreet("Street");

        OrderCustomerInfo orderCustomerInfo = new OrderCustomerInfo();
        orderCustomerInfo.setAddress(address);
        orderCustomerInfo.setEmail("jane.doe@example.org");
        orderCustomerInfo.setId(1L);
        orderCustomerInfo.setName("Name");
        orderCustomerInfo.setPhone("6625550144");
        orderCustomerInfo.setUsername("janedoe");

        // Act and Assert
        assertThrows(OrderCustomException.class, () -> orderService.updateOrderDelivered("42", orderCustomerInfo));
        verify(orderDeliveryRepository).findByOrderHeader(Mockito.<OrderHeader>any());
        verify(orderHeaderRepository).findByCustomerIdAndOrderNumber(anyLong(), Mockito.<String>any());
    }

}
