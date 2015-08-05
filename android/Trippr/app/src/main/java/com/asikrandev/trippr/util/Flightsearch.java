package com.asikrandev.trippr.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flightsearch {

    public String extractData(String message) {

        try {
            JSONObject json = new JSONObject(message);
            JSONObject dates = json.getJSONObject("Dates");
            JSONArray outdates = dates.getJSONArray("OutboundDates");

            List prices = new ArrayList();
            int i = 0;
            while (i < 5) {
                i++;
                JSONObject priceList = outdates.getJSONObject(i);
                prices.add(priceList.getDouble("Price"));
            }

            return Collections.min(prices).toString();
        } catch (Exception e) {
            System.out.println("error flight search" + e.getLocalizedMessage());
        }
        return "0";
    }

    public String getUrl(String origin, String destination){
        return "http://partners.api.skyscanner.net/apiservices/browsedates/v1.0/US/USD/en-US/" + origin +"/" + destination + "/anytime/anytime?apiKey=4137fca3-67c1-481b-b8bd-4be5bfbe3051";
    }

}
