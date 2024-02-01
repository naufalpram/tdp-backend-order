package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.bean.catalog.OrderProductInfo;
import com.edts.tdp.batch4.bean.catalog.OrderProductResponse;
import com.edts.tdp.batch4.bean.catalog.OrderStockUpdate;
import com.edts.tdp.batch4.bean.customer.OrderCartBean;
import com.edts.tdp.batch4.constant.DeliveryFeeMultiplier;
import com.edts.tdp.batch4.constant.StaticGeoLocation;
import com.edts.tdp.batch4.bean.customer.OrderCustomerAddress;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderDelivery;
import com.edts.tdp.batch4.model.OrderDetail;
import com.edts.tdp.batch4.model.OrderHeader;
import com.edts.tdp.batch4.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class OrderLogicService {

    @Autowired
    private JwtUtil jwtUtil;

    public OrderLogicService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public static double distanceCounter(double lat, double lon ) {
        // Conversi sudut ke Radiant
        double radLat1 = Math.toRadians(StaticGeoLocation.LATITUDE);
        double radLat2 = Math.toRadians(lat);
        double radLon1 = Math.toRadians(StaticGeoLocation.LONGITUDE);
        double radLon2 = Math.toRadians(lon);
        // Menghitung Selisih jarak
        double selisihLat = radLat2 - radLat1;
        double selisihLon = radLon2 - radLon1;

        // Haversine Formula
        double variable = Math.sin(selisihLat/2) * Math.sin(selisihLat/2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(selisihLon/2) * Math.sin(selisihLon/2);

        double constant = 2 * Math.atan2(Math.sqrt(variable), Math.sqrt(1-variable));

        double distance = constant * StaticGeoLocation.EARTH_RADIUS;
        return distance;
    }

    public static double deliveryCost(double distance) {
        if ( distance <= 200) {
            return distance * DeliveryFeeMultiplier.UNDER_200;
        } else {
            return distance * DeliveryFeeMultiplier.OVER_200;
        }
    }

    public OrderCustomerInfo getCustomerInfo(HttpServletRequest httpServletRequest, String path) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);

        Boolean isValidToken = jwtUtil.validateToken(token, path);

        if ( !isValidToken ) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Token", path);
        }

        String url = "https://teaching-careful-lioness.ngrok-free.app/api/v1/customer/customer_info";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url,
                                                                HttpMethod.GET,
                                                                httpEntity,
                                                                String.class);
        if ( response.getStatusCode().equals(HttpStatus.OK)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.getBody(), OrderCustomerInfo.class);
            } catch (JsonProcessingException e) {
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), path);
            }
        }
        throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
    }

    public static OrderCustomerAddress getCustomerAddress(OrderCustomerInfo orderCustomerInfo) {
        OrderCustomerAddress orderCustomerAddress = orderCustomerInfo.getAddress();
        return orderCustomerAddress;
    }

    public static StringWriter createCsv(List<OrderHeader> orders) {
        StringWriter stringWriter = new StringWriter();

        CSVWriter csvWriter = new CSVWriter(stringWriter,';'
                ,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER
                ,CSVWriter.DEFAULT_LINE_END);

        final String CSV_HEADER = "Order Id,Order Number,Username,Alamat,Jarak,Total Pembayaran,Status";
        csvWriter.writeNext(CSV_HEADER.split(","));

        for(OrderHeader orderHeader:orders){
            String orderNumber = orderHeader.getOrderNumber() == null ? "" : orderHeader.getOrderNumber();
            OrderDelivery checked = checkOrderDelivery(orderHeader.getOrderDelivery());

            String address = checked.getStreet().isEmpty() || checked.getProvince().isEmpty() ? "Incomplete Address"
                    : String.format("%s, %s", checked.getStreet(), checked.getProvince());
            String distance = String.format("%.2f", checked.getDistanceInKm());
            String totalPaid = String.format("%.2f", orderHeader.getTotalPaid());
            csvWriter.writeNext(new String[]{
                    String.valueOf(orderHeader.getId()), orderNumber,
                    orderHeader.getCreatedBy(), address, distance, totalPaid, orderHeader.getStatus()
            });
        }

        stringWriter.flush();

        return stringWriter;
    }

    public static OrderProductResponse getAllProductInfo(List<Integer> arrayProduct) {
        String url = "https://proud-mongoose-shortly.ngrok-free.app/api/v1/catalog/order/product";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Integer>> httpEntity = new HttpEntity<>(arrayProduct, headers);

        RestTemplate restTemplate = new RestTemplate();

        OrderProductResponse response = restTemplate.postForObject(url, httpEntity, OrderProductResponse.class);
        return response;
    }

    public static String orderReportHtml(OrderHeader orderHeader, OrderProductResponse productDetails) {
        if (orderHeader == null)
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "No order", "/update/deliver");
        List<OrderDetail> list = orderHeader.getOrderDetailList();
        String htmlContent = "";
        StringBuilder productListStr = new StringBuilder();
        for (int i = 0; i < productDetails.getData().size(); i++) {
            OrderProductInfo item = productDetails.getData().get(i);
            productListStr.append(String.format("""
                        <li>
                           <p>%s %dx | Rp%.2f</p>
                        </li>
                    """, item.getProductName(), list.get(i).getQty(), item.getPrice() * list.get(i).getQty()));
        }
        htmlContent += String.format("""
                    <html>
                      <body>
                        <h1>Your Order Report</h1>
                        <div>
                          <h4>Order Number: %s</h4>
                          <h4>Total Paid: Rp%.2f</h4>
                          <ul>
                            %s
                          </ul>
                        </div>
                      </body>
                    </html>
                """, orderHeader.getOrderNumber(), orderHeader.getTotalPaid(), productListStr);
        return htmlContent;
    }

    private static OrderDelivery checkOrderDelivery(OrderDelivery toCheck) {
        OrderDelivery newOrder = new OrderDelivery();
        newOrder.setStreet("");
        newOrder.setProvince("");
        newOrder.setDistanceInKm(0);
        if (toCheck != null) {
            if (toCheck.getStreet() != null) newOrder.setStreet(toCheck.getStreet());
            if (toCheck.getProvince() != null) newOrder.setProvince(toCheck.getProvince());
            newOrder.setDistanceInKm(toCheck.getDistanceInKm());
        }
        return newOrder;
    }


    public static OrderStockUpdate updateStockProduct(List<OrderCartBean> arrayProduct, boolean isCreate) {
        if (isCreate) {
            for (OrderCartBean product : arrayProduct) {
                product.setQuantity(product.getQuantity() * -1);
            }
        }
        String url = "https://proud-mongoose-shortly.ngrok-free.app/api/v1/catalog/order/update";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<OrderCartBean>> httpEntity = new HttpEntity<>(arrayProduct, headers);

        RestTemplate restTemplate = new RestTemplate();

        OrderStockUpdate response = restTemplate.postForObject(url, httpEntity, OrderStockUpdate.class);
        return response;
    }

    public List<LinkedHashMap<String,Integer>> getCartData(HttpServletRequest httpServletRequest, String path) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);

        Boolean isValidToken = jwtUtil.validateToken(token, path);

        if ( !isValidToken ) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Token", path);
        }
        String url = "https://teaching-careful-lioness.ngrok-free.app/api/v1/customer/order_cart";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url,
                                                                HttpMethod.GET,
                                                                httpEntity,
                                                                String.class);
        if ( response.getStatusCode().equals(HttpStatus.OK)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.getBody(), List.class);
            } catch (JsonProcessingException e) {
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), path);
            }
        }
        throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
    }

    public Boolean clearCartCustomer(HttpServletRequest httpServletRequest, String path) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);

        Boolean isValidToken = jwtUtil.validateToken(token, path);

        if ( !isValidToken ) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Token", path);
        }
        String url = "https://teaching-careful-lioness.ngrok-free.app/api/v1/customer/clear_cart";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url,
                                                                HttpMethod.POST,
                                                                httpEntity,
                                                                String.class);
        return response.getStatusCode().equals(HttpStatus.OK);
    }
}
