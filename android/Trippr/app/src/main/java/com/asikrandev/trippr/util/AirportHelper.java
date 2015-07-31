package com.asikrandev.trippr.util;

import android.os.AsyncTask;
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

public class AirportHelper extends AsyncTask<String, Void, String> {

    private String url = "https://airport.api.aero/airport/nearest/";

    protected String getClosestAirport(String lat, String lng) {
        String apicall = url + lat + "/" + lng + "/?user_key=fc53ceaf759712f7fbd0d4846926d756";
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(apicall);
            httpget.setHeader("Accept", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpget);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            System.out.println("InputStream " + e.getLocalizedMessage());
        }

        Log.d("Trippr", result + "");
        return extractData(result);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line, result = "";

        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private static String extractData(String message) {

        try {
            JSONObject json = new JSONObject(message);
            JSONArray airport = json.getJSONArray("airports");

            JSONObject closest_airport = airport.getJSONObject(0);

            return closest_airport.getString("code");
        } catch (Exception e) {
            System.out.println("error flight search" + e.getLocalizedMessage());
        }
        return "LOL";
    }

    @Override
    protected String doInBackground(String... params) {
        return getClosestAirport(params[0], params[1]);
    }
}