package com.asikrandev.trippr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asikrandev.trippr.Adapters.TripprAdapter;
import com.asikrandev.trippr.CustomViews.OnSwipeListener;
import com.asikrandev.trippr.Entities.DestinationResponse;
import com.asikrandev.trippr.Entities.Hotel;
import com.asikrandev.trippr.Entities.TripprLocation;
import com.asikrandev.trippr.util.AirportHelper;
import com.asikrandev.trippr.util.CityCodeHelper;
import com.asikrandev.trippr.util.Flightsearch;
import com.asikrandev.trippr.Entities.Image;
import com.asikrandev.trippr.util.HotelHelper;
import com.asikrandev.trippr.util.ImageLoader;
import com.asikrandev.trippr.CustomViews.TripprSwipe;
import com.asikrandev.trippr.util.LocationHelper;
import com.asikrandev.trippr.util.PicsHelper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {

    private LinearLayout content;
    private LinearLayout buttonsLayout;
    private View resultLayout;
    private LinearLayout waitLayout;

    private TextView cityResultTV;
    private TextView countryResultTV;
    private TextView priceTV;
    private TextView fromCityTV;

    private ImageButton yesButton;
    private ImageButton noButton;

    private ArrayList<Image> list, allImages;
    private ArrayList<String> like, destination;

    private TripprLocation mCurrentLocation;
    private String mCurrentCity;
    private String mCurrentCityCode;
    private String city;
    private String country;
    private String cheapestPrice;

    private TripprSwipe swipe;
    private TripprAdapter tripprAdapter;
    private String resultImageUrl;
    private String picsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LocationHelper locationHelper = new LocationHelper(this);
        locationHelper.execute();
        locationHelper.setLocationHelperListener(new LocationHelper.LocationHelperListener() {
            @Override
            public void onResult(TripprLocation location) {
                mCurrentLocation = location;
                destination.add(mCurrentLocation.city);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        like = new ArrayList<>();
        destination = new ArrayList<>();
        mCurrentCity = "";
        mCurrentLocation = new TripprLocation();

//        LocationHelper locationHelper = new LocationHelper(this);
//        locationHelper.execute();
//        locationHelper.setLocationHelperListener(new LocationHelper.LocationHelperListener() {
//            @Override
//            public void onResult(TripprLocation location) {
//                mTripprLocation = location;
//            }
//        });

//        findCurrentCityCode();
        setFormViews();
        getImages2();
    }

    private void setFormViews() {
        content = (LinearLayout) findViewById(R.id.tripper_swipe);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttons);
        yesButton = (ImageButton) findViewById(R.id.yesButton);
        noButton = (ImageButton) findViewById(R.id.noButton);

        waitLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.waiting, null);

        resultLayout = getLayoutInflater().inflate(R.layout.result, null);
        cityResultTV = (TextView) resultLayout.findViewById(R.id.city);
        countryResultTV = (TextView) resultLayout.findViewById(R.id.country);
        priceTV = (TextView) resultLayout.findViewById(R.id.price);
        fromCityTV = (TextView) resultLayout.findViewById(R.id.from);
    }

    public Context getContext() {
        return this;
    }

    private void setSwipe() {
        tripprAdapter = new TripprAdapter(this, list);
        swipe = new TripprSwipe(getContext(), false);
        swipe.setAdapter(tripprAdapter);
        swipe.setFit(true);
        swipe.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onLike(int position) {
                like.add(getFormattedTags(position));
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

                buttonsLayout.removeView(yesButton);
                buttonsLayout.removeView(noButton);
                content.removeView(swipe);
                content.addView(waitLayout);

                getDreamDestination2();

            }
        });

        LinearLayout.LayoutParams swipeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        swipeParams.gravity = Gravity.CENTER;
        swipe.setLayoutParams(swipeParams);
        content.addView(swipe);
    }

    private String getFormattedTags(int position) {
        String formattedTags = "";
        for (int i = 0; i < tripprAdapter.getItem(position).getTags().length; i++)
            formattedTags += tripprAdapter.getItem(position).getTags()[i] + " ";
        return formattedTags;
    }

    public void getImages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/v1/pics";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        String[] images = httpResponse.split(";");
                        allImages = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            String imageData[] = images[i].split(":");
                            String imageLink = imageData[0] + ":" + imageData[1];
                            String tags[] = imageData[2].split(",");

                            ImageLoader imageLoader = new ImageLoader();
                            imageLoader.execute(imageLink);
                            Bitmap bitmap = null;
                            try {
                                bitmap = imageLoader.get();
                            } catch (Exception e) {
                                Log.d("Trippr", e.getMessage());
                            }
                            allImages.add(new Image(i, imageLink, bitmap, tags));
                        }
                        setSwipe();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        queue.add(stringRequest);
    }

    public void getImages2() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/v1/pics";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        picsResponse = httpResponse;
                        PicsHelper picsHelper = new PicsHelper(MainActivity.this);
                        picsHelper.execute(picsResponse);
                        picsHelper.setPicsHelperListener(new PicsHelper.PicsHelperListener() {
                            @Override
                            public void onResult(ArrayList<Image> images) {
                                list = images;
                                setSwipe();
                                findViewById(R.id.swipe).setVisibility(View.VISIBLE);
                                findViewById(R.id.splash).setVisibility(View.GONE);

                                PicsHelper cachedPicsHelper = new PicsHelper(MainActivity.this);
                                cachedPicsHelper.execute(picsResponse);
                                cachedPicsHelper.setPicsHelperListener(new PicsHelper.PicsHelperListener() {
                                    @Override
                                    public void onResult(ArrayList<Image> images) {
                                        allImages = images;
                                    }
                                });
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        queue.add(stringRequest);
    }

    public void getDreamDestination() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        if (httpResponse.equals("")) {
                            setErrorResponseView();
                        } else {
                            String[] response = httpResponse.split(":");

                            city = response[0];
                            country = response[1];
                            String countryCode = response[2];
                            String destinationCode;
                            resultImageUrl = "";
                            if (response.length > 3) {
                                destinationCode = response[3];
                                resultImageUrl = response[4] + ":" + response[5];
                            } else {
                                CityCodeHelper cityCodeHelper = new CityCodeHelper();
                                destinationCode = cityCodeHelper.findCityCode(city, countryCode, getContext());
                            }

                            getCheapestPrice(mCurrentCityCode, destinationCode);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorResponseView();
                    }
                }
        ) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("text", getStrings(like));
                params.put("excluded", getStrings(destination));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void getDreamDestination2() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/v1/search";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        if (httpResponse.equals("")) {
                            setErrorResponseView();
                        } else {
                            Gson gson = new Gson();
                            DestinationResponse destinationResponse = gson.fromJson(httpResponse, DestinationResponse.class);

                            city = destinationResponse.city;
                            country = destinationResponse.country;
                            String countryCode = destinationResponse.countrycode;
                            String destinationCode = destinationResponse.airportcode;
                            resultImageUrl = destinationResponse.banner;

                            getCheapestPrice(mCurrentLocation.cityCode, destinationCode);
                            showHotel(destinationResponse.booking_city_id);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorResponseView();
                    }
                }
        ) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("text", getStrings(like));
                params.put("excluded", getStrings(destination));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void showHotel(final String bookingCityId) {
        final HotelHelper hotelHelper = new HotelHelper();

        RequestQueue queue = Volley.newRequestQueue(this);

        Calendar arrival = Calendar.getInstance();
        arrival.add(Calendar.MONTH, 2);
        arrival.set(Calendar.DAY_OF_MONTH, 15);

        Calendar departure = Calendar.getInstance();
        departure.add(Calendar.MONTH, 2);
        departure.set(Calendar.DAY_OF_MONTH, 18);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String arrivalStr = simpleDateFormat.format(arrival.getTime());
        final String departureStr = simpleDateFormat.format(departure.getTime());

        String url = hotelHelper.getUrl(bookingCityId,
                arrivalStr,
                departureStr
        );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        Hotel hotel = hotelHelper.extractData(httpResponse);
                        String link = "http://www.booking.com/searchresults.html?checkin="
                                + arrivalStr
                                + ";checkout="
                                + departureStr
                                + ";city="
                                + bookingCityId
                                + ";dcsc=2&aid=847695";
                        setHotelView(link, String.valueOf(hotel.price));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorResponseView();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic cnZlZ2FzOmF0azU5cGwx");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setHotelView(final String link, String hotelPrice) {
        ImageView hotelImage = (ImageView) resultLayout.findViewById(R.id.hotel_image);
        hotelImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                registerHit();
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });

        TextView hotelResultView = (TextView) resultLayout.findViewById(R.id.hotel_price);
        hotelResultView.setText("$" + hotelPrice);
        resultLayout.findViewById(R.id.hotel_progress).setVisibility(View.GONE);
        resultLayout.findViewById(R.id.hotel_container).setVisibility(View.VISIBLE);

    }

    private void getClosestAirport(double lat, double lon) {
        final AirportHelper airportHelper = new AirportHelper();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, airportHelper.getUrl(lat, lon),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        mCurrentCityCode = airportHelper.extractData(httpResponse);
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

    private void getCheapestPrice(String currentCityCode, final String destinationCode) {
        final Flightsearch search = new Flightsearch();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, search.getUrl(currentCityCode, destinationCode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                        cheapestPrice = search.extractData(httpResponse);
                        setResultView(resultImageUrl, destinationCode, cheapestPrice);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(stringRequest);
    }

    private void registerHit() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://104.131.72.2:80/v1/hit";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String httpResponse) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("text", getStrings(like));
                params.put("result", city);
                return params;
            }
        };
        queue.add(stringRequest);
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
    }

    private void setResultView(String imageUrl, String destinationCode, String cheapestPrice) {
        final String link = "http://www.skyscanner.com/transport/flights/" + mCurrentLocation.cityCode + "/" + destinationCode + "/";
        final ImageView destinationImage = (ImageView) resultLayout.findViewById(R.id.destination_image);
        ImageView resultImage = (ImageView) resultLayout.findViewById(R.id.result_image);
        resultImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                registerHit();
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.execute(imageUrl);
        imageLoader.setImageLoaderListener(new ImageLoader.ImageLoaderListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                try {
                    destinationImage.setImageBitmap(bitmap);
                    cityResultTV.setText(city);
                    countryResultTV.setText(country);
                    findViewById(R.id.destination_progress).setVisibility(View.GONE);
                    findViewById(R.id.destination_result).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.d("Trippr", e.getMessage());
                }

            }
        });

        priceTV.setText("$" + cheapestPrice);
        fromCityTV.setText(mCurrentLocation.cityCode + " - " + destinationCode);

        destination.add(city);

        content.removeView(waitLayout);
        if (resultLayout.getParent() == null) {
            content.addView(resultLayout);
        }
    }

    /**
     * Get current city from location info
     */
    protected void findCurrentCityCode() {
        LocationManager mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = 0;
        double lon = 0;
        if (location == null) {
            location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }

        Geocoder gcd = new Geocoder(this.getContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                mCurrentCity = addresses.get(0).getLocality();
                getClosestAirport(lat, lon);
            } else {
                mCurrentCity = "BERL";
                mCurrentCityCode = "BER";
            }
            destination.add(mCurrentCity);

        } catch (Exception e) {
            Log.d("Trippr", e.getMessage());
        }
    }

    // Action Buttons
    public void like(View view) {
        swipe.like();
    }

    public void dontLike(View view) {
        swipe.dontLike();
    }

    public void restart(View view) {
        resultLayout.findViewById(R.id.hotel_progress).setVisibility(View.VISIBLE);
        resultLayout.findViewById(R.id.hotel_container).setVisibility(View.GONE);
        findViewById(R.id.destination_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.destination_result).setVisibility(View.GONE);
        resultLayout.findViewById(R.id.hotel_image).setOnClickListener(null);


        list = (ArrayList<Image>) allImages.clone();

        PicsHelper picsHelper = new PicsHelper(MainActivity.this);
        picsHelper.execute(picsResponse);
        picsHelper.setPicsHelperListener(new PicsHelper.PicsHelperListener() {
            @Override
            public void onResult(ArrayList<Image> images) {
                allImages = images;
            }
        });

        tripprAdapter.clear();
        tripprAdapter.setItems(list);
        swipe.restart();

        content.removeAllViews();
        content.addView(swipe);

        buttonsLayout.addView(noButton);
        buttonsLayout.addView(yesButton);
    }

    public String getStrings(ArrayList<String> list) {
        String query = "";
        int i;
        for (i = 0; i < list.size() - 1; i++) {
            query = query + list.get(i) + " ";
        }
        if (list.size() > 0) query = query + list.get(i);
        return query;
    }
}
