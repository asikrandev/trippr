package com.asikrandev.trippr.util;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flightsearch extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] params) {
        String origin = params[0];
        String destination = params[1];
        String search = trySkyscannerApi(origin, destination);

        return extractData(search);
    }

    protected String trySkyscannerApi(String origin, String destination) {
        String url = "http://partners.api.skyscanner.net/apiservices/browsedates/v1.0/US/USD/en-US/" + origin +"/" + destination + "/anytime/anytime?apiKey=4137fca3-67c1-481b-b8bd-4be5bfbe3051";
        Log.d("searching in", url);
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpget);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            System.out.println("InputStream " + e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line, result = "";

        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private static String extractData(String message) {

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

}
