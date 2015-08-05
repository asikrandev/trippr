package com.asikrandev.trippr.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class AirportHelper {

    private String url = "https://airport.api.aero/airport/nearest/";

    public String extractData(String message) {
        String JsonResponse = message.substring( 9, message.length() - 1 );
        try {
            JSONObject json = new JSONObject(JsonResponse);
            JSONArray airport = json.getJSONArray("airports");

            JSONObject closest_airport = airport.getJSONObject(0);

            return closest_airport.getString("code");
        } catch (Exception e) {
            System.out.println("error flight search" + e.getLocalizedMessage());
        }
        return "LOL";
    }

    public String getUrl(double lat, double lon) {
        return url + String.valueOf( lat ) + "/" + String.valueOf( lon ) + "/?user_key=fc53ceaf759712f7fbd0d4846926d756";
    }
}