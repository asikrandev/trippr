package com.asikrandev.trippr.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asikrandev.trippr.Entities.TripprLocation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jegasmlm on 9/3/2015.
 */
public class LocationHelper extends AsyncTask<String, Void, TripprLocation> {

    private final Context mContext;

    public LocationHelperListener locationHelperListener;
    private TripprLocation tripprLocation;

    public void setLocationHelperListener(LocationHelperListener locationHelperListener) {
        this.locationHelperListener = locationHelperListener;
    }

    public LocationHelper(Context context) {
        mContext = context;
    }

    @Override
    protected TripprLocation doInBackground(String... params) {

        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = 0;
        double lon = 0;
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = location.getLatitude();
            lon = location.getLongitude();
        } else {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }

        tripprLocation = new TripprLocation();

        try {
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                tripprLocation.city = addresses.get(0).getLocality();
                getClosestAirport(lat, lon);
            } else {
                tripprLocation.city = "BERL";
                tripprLocation.cityCode = "BER";
            }
        } catch (Exception e) {
            Log.d("Trippr", e.getMessage());
        }

        return tripprLocation;

    }

    @Override
    protected void onPostExecute(TripprLocation location) {
        if (locationHelperListener != null) locationHelperListener.onResult(location);
    }

    public interface LocationHelperListener {
        void onResult(TripprLocation location);
    }


    private void getClosestAirport(double lat, double lon) {
        final AirportHelper airportHelper = new AirportHelper();
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, airportHelper.getUrl(lat, lon),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        tripprLocation.cityCode = airportHelper.extractData(httpResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return null;
            }
        };
        queue.add(stringRequest);
    }
}
