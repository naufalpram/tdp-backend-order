package com.edts.tdp.batch4.service;

import com.edts.tdp.batch4.constant.DeliveryFeeMultiplier;
import com.edts.tdp.batch4.constant.StaticGeoLocation;
import org.springframework.stereotype.Service;

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
}
