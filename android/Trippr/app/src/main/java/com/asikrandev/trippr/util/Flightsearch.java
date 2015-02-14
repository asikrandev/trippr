package com.asikrandev.trippr.util;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Flightsearch extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] params) {
        String search = trySkyscannerApi();

        return extractData(search);
    }

    protected String trySkyscannerApi() {
        String url = "http://partners.api.skyscanner.net/apiservices/browsedates/v1.0/US/USD/en-US/BER/NYC/anytime/anytime?apiKey=4137fca3-67c1-481b-b8bd-4be5bfbe3051";

        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            System.out.println("InputStream " + e.getLocalizedMessage());
        }

        return "Result is " + result;
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

        return "";
    }

}
