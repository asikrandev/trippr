package com.asikrandev.trippr.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ricardovegas on 2/15/15.
 */
public class CityCodeHelper {

    // CSV File Name
    private static final String CSVFILENAME = "databases/citycodes.csv";

    public String findCityCode(String city, String country, Context context) {

        try {
            InputStream myInput = context.getAssets().open(CSVFILENAME);

            BufferedReader reader = new BufferedReader(new InputStreamReader(myInput));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                if ((rowData[3].equals("\"" + city + "\"") || (rowData.length > 4 && rowData[4].equals("\"" + city + "\"") ))&& rowData[1].equals("\"" + country + "\"")) {
                    Log.d("found place", rowData[1] + rowData[2] + rowData[3]);
                    return rowData[2].replace("\"", "");
                }
                // do something with "data" and "value"
            }
            myInput.close();
        }
        catch (IOException ex) {
            Log.d("csvreader", ex.getMessage());
        }
        return "";
    }

}
