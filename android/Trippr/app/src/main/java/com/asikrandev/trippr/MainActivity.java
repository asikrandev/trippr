package com.asikrandev.trippr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asikrandev.trippr.util.AirportHelper;
import com.asikrandev.trippr.util.CityCodeHelper;
import com.asikrandev.trippr.util.Flightsearch;
import com.asikrandev.trippr.util.Image;
import com.asikrandev.trippr.util.MySQLiteHelper;
import com.asikrandev.trippr.util.TripprSwipe;
import com.joanzapata.android.iconify.Iconify;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;


public class MainActivity extends Activity {

    private LinearLayout content;
    private LinearLayout buttonsLayout;
    private LinearLayout resultLayout;
    private LinearLayout waitLayout;

    private TextView cityResultTV;
    private TextView countryResultTV;
    private TextView priceTV;
    private TextView fromCityTV;
    private TextView waitTV;

    private ImageButton restartButton;
    private ImageButton yesButton;
    private ImageButton noButton;

    private ArrayList<Image> list, allImages;
    private ArrayList<String> like, destination;

    private String mCurrentCityCode;
    private String city;
    private String country;
    private String cheapestPrice;
    private String fromCity;

    private TripprSwipe swipe;

    private void init() {
        mCurrentCityCode = findCurrentCityCode();

        Calendar time;

        like = new ArrayList<String>();
        destination = new ArrayList<String>();

        File database = getApplicationContext().getDatabasePath(MySQLiteHelper.DATABASE_NAME);

        MySQLiteHelper db = new MySQLiteHelper(this);
        if (!database.exists()) {
            db.loadDB();
        }

        // get Images from database
        allImages = db.getAllImages();

        ArrayList<Image> subSet = (ArrayList<Image>) allImages.clone();

        Collections.shuffle(subSet);

        Random random = new Random();
        int roundCount = random.nextInt((10 - 4) + 1) + 5;
        list = new ArrayList<Image>(subSet.subList(0, roundCount));

        content = (LinearLayout) findViewById(R.id.tripper_swipe);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttons);
        yesButton = (ImageButton) findViewById(R.id.yesButton);
        noButton = (ImageButton) findViewById(R.id.noButton);

        waitLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.waiting, null);
        waitTV = (TextView) waitLayout.findViewById(R.id.wait);
        Iconify.addIcons(waitTV);

        time = Calendar.getInstance();
        resultLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.result, null);
        cityResultTV = (TextView) resultLayout.findViewById(R.id.city);
        countryResultTV = (TextView) resultLayout.findViewById(R.id.country);
        priceTV = (TextView) resultLayout.findViewById(R.id.price);
        fromCityTV = (TextView) resultLayout.findViewById(R.id.from);
        Log.d("Trippr", "time: " + (Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis()));

        restartButton = (ImageButton) LayoutInflater.from(this).inflate(R.layout.restart_button, buttonsLayout, false);

        time = Calendar.getInstance();
        swipe = new TripprSwipe(this, list, false);
        Log.d("Trippr", "time: " + (Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis()));
        swipe.setFit(true);
        swipe.setOnSwipeListener(new TripprSwipe.onSwipeListener() {
            @Override
            public void onLike(int position) {
                like.add(list.get(position).getTags());
                yesButton.setBackgroundColor(0xBB33B5E5);
                yesButton.setEnabled(false);
                noButton.setEnabled(false);
            }

            @Override
            public void onLikeAnimationEnd() {
                yesButton.setBackgroundResource(R.drawable.button_selector);
                yesButton.setEnabled(true);
                noButton.setEnabled(true);
            }

            @Override
            public void onDislike() {
                noButton.setBackgroundColor(0xBB33B5E5);
                yesButton.setEnabled(false);
                noButton.setEnabled(false);
            }

            @Override
            public void onDislikeAnimationEnd() {
                noButton.setBackgroundResource(R.drawable.button_selector);
                yesButton.setEnabled(true);
                noButton.setEnabled(true);
            }

            @Override
            public void onFinished() {

                Iconify.addIcons(waitTV);
                waitTV.setText("{fa-android}");
                cityResultTV.setText("{fa-cog}");
                Iconify.addIcons(cityResultTV);
                buttonsLayout.removeView(yesButton);
                buttonsLayout.removeView(noButton);
                content.removeView(swipe);
                content.addView(waitLayout);

                waitTV.setText("{fa-cog}");
                Iconify.addIcons(waitTV);

                int loops = 1000000;
                int degreesPerSecond = 360;
                waitTV.animate().rotationBy(degreesPerSecond * loops).setDuration(loops * 10000)
                        .setInterpolator(new LinearInterpolator());

                requestHttp();

            }
        });

        LinearLayout.LayoutParams swipeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        swipeParams.gravity = Gravity.CENTER;
        swipe.setLayoutParams(swipeParams);
        content.addView(swipe);
    }

    public Context getContext() {
        return (Context) this;
    }

    public String getCityCode() {
        if (this.mCurrentCityCode == null) {
            return findCurrentCityCode();
        }
        return this.mCurrentCityCode;
    }

    public String getCityName() {
        if (this.fromCity == null) {
            return findCurrentCityCode();
        }
        return this.fromCity;
    }

    public void requestHttp() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        if(httpResponse.equals("")){
                            setErrorResponseView();
                        }else {
                            String[] response = httpResponse.split(":");

                            city = response[0];
                            country = response[1];
                            String countryCode = response[2];
                            String destinationCode;
                            if (response.length == 4) {
                                destinationCode = response[3];
                            } else {
                                CityCodeHelper cityCodeHelper = new CityCodeHelper();
                                destinationCode = cityCodeHelper.findCityCode(city, countryCode, getContext());
                            }

                            String cheapestPrice = getCheapestPrice(mCurrentCityCode, destinationCode);
                            setResultView(destinationCode, cheapestPrice);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorResponseView();
            }
        }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("text", getStrings(like));
                params.put("excluded", getStrings(destination));
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String getCheapestPrice(String currentCityCode, String destinationCode) {
        Flightsearch search = new Flightsearch();
        search.execute(currentCityCode, destinationCode);
        String cheapestPrice = "";
        try {
            cheapestPrice = search.get();
        } catch (Exception e) {
            System.out.println("error in get");
        }
        return cheapestPrice;
    }

    private void setErrorResponseView() {
        View errorResponseView = LayoutInflater.from(this).inflate(R.layout.error_response, null);

        final String link = "http://www.virgingalactic.com";
        ImageView resultImage = (ImageView) errorResponseView.findViewById(R.id.result_image);
        resultImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });

        content.removeView(waitLayout);
        content.addView(errorResponseView);
        buttonsLayout.addView(restartButton);
    }

    private void setResultView(String destinationCode, String cheapestPrice) {
        final String link = "http://www.skyscanner.com/transport/flights/" + mCurrentCityCode + "/" + destinationCode + "/";
        ImageView resultImage = (ImageView) resultLayout.findViewById(R.id.result_image);
        resultImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });

        cityResultTV.setText(city);
        countryResultTV.setText(country);
        priceTV.setText("$" + cheapestPrice);
        fromCityTV.setText(mCurrentCityCode + " - " + destinationCode);

        destination.add(city);

        content.removeView(waitLayout);
        if (resultLayout.getParent() == null) {
            content.addView(resultLayout);
            buttonsLayout.addView(restartButton);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.mCurrentCityCode = findCurrentCityCode();
    }

    /**
     * Get current city from location info
     */
    protected String findCurrentCityCode() {
        LocationManager mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Geocoder gcd = new Geocoder(this.getContext(), Locale.getDefault());

        if (location == null) {
            location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        String code = "";

        try {
            AirportHelper airportHelper = new AirportHelper();
            airportHelper.execute(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            String cityCode = "";
            try {
                cityCode = airportHelper.get();
            } catch (Exception e) {
                Log.d("Trippr","error in get");
            }
            Log.d("Trippr", "CityCode:" + cityCode);
            return cityCode;

        } catch (Exception e) {
            Log.d("Trippr", e.getMessage());
        }

        return "BER";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Bitmap> getBitmapList() {
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

        for (int i = 0; i < list.size(); i++) {
            Resources res = getResources();
            int resID = res.getIdentifier(list.get(i).getSource(), "drawable", getPackageName());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bm = BitmapFactory.decodeResource(getResources(), resID, options);

            bitmapList.add(bm);
        }

        return bitmapList;
    }

    // Action Buttons
    public void like(View view) {
        swipe.like();
    }

    public void dontLike(View view) {
        swipe.dontLike();
    }

    public void restart(View view) {

        ArrayList<Image> subSet = (ArrayList<Image>) allImages.clone();

        Collections.shuffle(subSet);

        Random random = new Random();
        int roundCount = random.nextInt((10 - 5) + 1) + 5;
        list = new ArrayList<Image>(subSet.subList(0, roundCount));

        swipe.loadNewBitmapList(list);
        swipe.restart();

        content.removeAllViews();
        content.addView(swipe);

        buttonsLayout.removeView(restartButton);
        buttonsLayout.addView(noButton);
        buttonsLayout.addView(yesButton);
    }

    public String getStrings(ArrayList<String> list) {
        String query = "";
        int i;
        for (i = 0; i < list.size() - 1; i++) {
            query = query + list.get(i) + ",";
        }
        if (list.size() > 0) query = query + list.get(i);
        return query;
    }
}
