package com.asikrandev.trippr.util;

import com.asikrandev.trippr.Entities.Hotel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jegasmlm on 9/2/2015.
 */
public class HotelHelper {

    public Hotel extractData(String message) {

        try {
            JSONArray data = new JSONArray(message);

            ArrayList<Hotel> hotels = new ArrayList();

            Hotel minHotel = new Hotel();
            minHotel.price = Double.MAX_VALUE;
            for(int i=0; i<data.length(); i++){
                if(data.getJSONObject(i).getDouble("min_price") < minHotel.price) {
                    minHotel.price = (data.getJSONObject(i).getDouble("min_price"));
                    minHotel.id = (data.getJSONObject(i).getString("hotel_id"));
                }
            }

            return minHotel;
        } catch (Exception e) {
            System.out.println("error flight search" + e.getLocalizedMessage());
        }
        return null;
    }

    public String getUrl(String bookingCityId, String arrival, String departure){

        return "https://distribution-xml.booking.com/json/bookings.getHotelAvailability?city_ids="
                + bookingCityId
                +"&arrival_date="
                +arrival
                +"&departure_date="
                +departure
                +"&guest_qty=2&available_rooms=1&classes=4,5&rows=10";
    }
}
