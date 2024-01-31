package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.constant.DeliveryFeeMultiplier;
import com.edts.tdp.batch4.constant.StaticGeoLocation;
import com.edts.tdp.batch4.bean.customer.OrderCustomerAddress;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.edts.tdp.batch4.model.OrderDelivery;
import com.edts.tdp.batch4.model.OrderHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.List;

@Service
public class OrderLogicService {
    public static double distanceCounter( double lat, double lon ) {
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

    public static OrderCustomerInfo getCustomerInfo(HttpServletRequest httpServletRequest, String path) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
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
            OrderCustomerInfo orderCustomerInfo = null;
            try {
                orderCustomerInfo = objectMapper.readValue(response.getBody(), OrderCustomerInfo.class);
            } catch (JsonProcessingException e) {
                throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), path);
            }
            return orderCustomerInfo;
        }
        throw new OrderCustomException(HttpStatus.BAD_REQUEST, "Invalid Customer Id", path);
    }

    public static OrderCustomerAddress getCustomerAddress(OrderCustomerInfo orderCustomerInfo) {
        OrderCustomerAddress orderCustomerAddress = orderCustomerInfo.getAddress();
        return orderCustomerAddress;
    }

    public static StringWriter createCsv(List<OrderHeader> orders) {
        OrderDelivery defaultDelivery = new OrderDelivery();
        defaultDelivery.setDistanceInKm(199);
        defaultDelivery.setStreet("Jl. Pegangsaan Timur no.99, Sudirman");
        defaultDelivery.setProvince("DKI Jakarta");

        StringWriter stringWriter = new StringWriter();

        CSVWriter csvWriter = new CSVWriter(stringWriter,';'
                ,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER
                ,CSVWriter.DEFAULT_LINE_END);

        final String CSV_HEADER = "Order Id,Order Number,Nama Customer,Alamat,Jarak,Total Pembayaran,Status";
        csvWriter.writeNext(CSV_HEADER.split(","));

        for(OrderHeader orderHeader:orders){
            OrderDelivery delivery = orderHeader.getOrderDelivery() == null ? defaultDelivery : orderHeader.getOrderDelivery();
            String orderNumber = orderHeader.getOrderNumber() == null ? "" : orderHeader.getOrderNumber();
            String street = delivery.getStreet() == null ? "" : delivery.getStreet();
            String province = delivery.getProvince() == null ? "" : delivery.getProvince();

            String address = String.format("%s, %s", street, province);
            String distance = String.format("%.2f", delivery.getDistanceInKm());
            String totalPaid = String.format("%.2f", orderHeader.getTotalPaid());
            csvWriter.writeNext(new String[]{
                    String.valueOf(orderHeader.getId()), orderNumber,
                    "dummy", address, distance, totalPaid, orderHeader.getStatus()
            });
        }

        stringWriter.flush();

        return stringWriter;
    }
}
