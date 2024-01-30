package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.constant.DeliveryFeeMultiplier;
import com.edts.tdp.batch4.constant.StaticGeoLocation;
import com.edts.tdp.batch4.bean.customer.OrderCustomerAddress;
import com.edts.tdp.batch4.bean.customer.OrderCustomerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

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

    public static OrderCustomerInfo getCustomerInfo(HttpServletRequest httpServletRequest) throws JsonProcessingException {
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
        ObjectMapper objectMapper = new ObjectMapper();
        OrderCustomerInfo orderCustomerInfo = objectMapper.readValue(response.getBody(), OrderCustomerInfo.class);

        return orderCustomerInfo;
    }

    public static OrderCustomerAddress getCustomerAddress(OrderCustomerInfo orderCustomerInfo) {
        OrderCustomerAddress orderCustomerAddress = orderCustomerInfo.getAddress();
        return orderCustomerAddress;
    }
}
