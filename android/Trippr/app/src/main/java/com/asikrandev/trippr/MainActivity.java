package com.asikrandev.trippr;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acision.acisionsdk.AcisionSdk;
import com.acision.acisionsdk.AcisionSdkCallbacks;
import com.acision.acisionsdk.AcisionSdkConfiguration;
import com.acision.acisionsdk.messaging.Messaging;
import com.acision.acisionsdk.messaging.MessagingReceiveCallbacks;
import com.acision.acisionsdk.messaging.MessagingReceivedMessage;
import com.acision.acisionsdk.messaging.MessagingSendOptions;
import com.asikrandev.trippr.util.CityCodeHelper;
import com.asikrandev.trippr.util.Flightsearch;
import com.asikrandev.trippr.util.Image;
import com.asikrandev.trippr.util.MySQLiteHelper;
import com.asikrandev.trippr.util.TripprSwipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private FrameLayout swipeContent;
    private LinearLayout buttonsLayout;
    private TextView result;
    private ImageButton restartButton;
    private ImageButton yesButton;
    private ImageButton noButton;

    private ArrayList<Image> list, allImages;
    private ArrayList<String> like, destination;
    private int position;
    private String city;
    private String cityCode;

    private String cheapestPrice;

    private AcisionSdk acisionSdk;
    private Messaging messaging;

    private TripprSwipe swipe;

    private void init(){
        like = new ArrayList<String>();
        destination = new ArrayList<String>();

        // delete databases created
        this.deleteDatabase("trippr");
        // creating new database
        MySQLiteHelper db = new MySQLiteHelper(this);

        db.loadDB();

        // get Images from database
        allImages = db.getAllImages();

        ArrayList<Image> subSet =  (ArrayList<Image>)allImages.clone();

        Collections.shuffle(subSet);

        Random random = new Random();
        int roundCount = random.nextInt((10 - 4) + 1) + 5;
        list =  new ArrayList<Image>(subSet.subList(0,roundCount));

        swipeContent = (FrameLayout) findViewById(R.id.tripper_swipe);

        buttonsLayout = (LinearLayout) findViewById(R.id.buttons);

        yesButton = (ImageButton) findViewById(R.id.yesButton);
        noButton = (ImageButton) findViewById(R.id.noButton);

        swipe = new TripprSwipe(this, getBitmapList(), false);
        swipe.setFit(true);
        swipe.setOnSwipeListener(new TripprSwipe.onSwipeListener() {
            @Override
            public void onLike(int position) {
                like.add(list.get(position).getTags());
            }

            @Override
            public void onFinished() {
                String query = getQuery();

                doSend(query);

                result.setText("wait...");
                swipeContent.removeView(swipe);
                swipeContent.addView(result);

                buttonsLayout.removeView(yesButton);
                buttonsLayout.removeView(noButton);
                buttonsLayout.addView(restartButton);
            }
        });

        FrameLayout.LayoutParams swipeParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        swipeParams.gravity = Gravity.CENTER;
        swipe.setLayoutParams(swipeParams);
        swipeContent.addView(swipe);

        result = new TextView(this);
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvParams.gravity = Gravity.CENTER;
        result.setLayoutParams(tvParams);
        result.setTextSize(50);
        result.setTextColor(getResources().getColor(R.color.lightgray));

        restartButton = new ImageButton(this);
        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParam.gravity = Gravity.CENTER_HORIZONTAL;
        buttonParam.setMargins(2, 2, 2, 2);
        restartButton.setLayoutParams(buttonParam);
        restartButton.setImageResource(R.drawable.restart);
        restartButton.setBackgroundResource(R.drawable.button_selector);
        restartButton.setPadding(10, 10, 10, 10);
        restartButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                restart();
            }
        });

    }



    private void start() {
        AcisionSdkConfiguration config = new AcisionSdkConfiguration("wvatXmaKZcmM", "jegasmlm_gmail_com_0", "3Ph0jsEHe");
        config.setPersistent(true);
        config.setApplicationActivity(this);
        acisionSdk = new AcisionSdk(config, new AcisionSdkCallbacks() {
            @Override
            public void onConnected(AcisionSdk acisionSdk) {
                TextView editMessage = (TextView) findViewById(R.id.text_display);
                editMessage.setText("Connected");
                yesButton.setEnabled(true);
                noButton.setEnabled(true);
                // Now start the messaging. /
                messaging = acisionSdk.getMessaging();
                testMessaging(messaging);
            }
        });
    }

    private void testMessaging(Messaging messaging) {
        messaging.setCallbacks(new MessagingReceiveCallbacks() {
            @Override
            public void onMessageReceived(Messaging messaging, MessagingReceivedMessage data) {
                result.setText(data.getContent());
                destination.add(data.getContent());
            }
        });
    }

    public void doSend(String message) {
        messaging.sendToDestination("jegasmlm_gmail_com_1", message, new MessagingSendOptions());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        start();

        getCurrentCityName();

        CityCodeHelper cityCodeHelper = new CityCodeHelper();

        //TODO: get destination code
        String destinationCode = "NYC";

        Flightsearch search = new Flightsearch();
        search.execute(this.cityCode, destinationCode);

        String getmessage = "error";
        try {
            this.cheapestPrice = search.get();
        } catch (Exception e) {
            System.out.println("error in get");
        }
        Toast toast = Toast.makeText(this.getApplicationContext(), this.cheapestPrice, Toast.LENGTH_LONG);
        toast.show();

    }

    /**
     * Get current city from location info
     */
    protected void getCurrentCityName() {
        LocationManager mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Geocoder gcd = new Geocoder(this.getApplicationContext(), Locale.getDefault());

        this.cityCode = "BKK";

        try {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = "empty";
            if (addresses.size() > 0) {
                address = addresses.get(0).getLocality();
            }

            this.city = address;

            CityCodeHelper cityCodeHelper = new CityCodeHelper();

            String countryCode = addresses.get(0).getCountryCode();
            String code = cityCodeHelper.findCityCode(address, countryCode, this.getApplicationContext());

            this.cityCode = code;

            String message = "You are in " + address + ", code is " + code +" ;)";
            Toast toast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

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

    public ArrayList<Bitmap> getBitmapList(){
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

        for(int i = 0; i < list.size(); i++){
            Resources res = getResources();
            int resID = res.getIdentifier(list.get(i).getSource(), "drawable", getPackageName());

            /*final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;*/

            Bitmap bm = BitmapFactory.decodeResource(getResources(), resID);

            bitmapList.add(bm);
        }

        return bitmapList;
    }

    // Action Buttons
    public void like(View view){
        swipe.like();
    }

    public void dontLike(View view){
        swipe.dontLike();
    }

    public void restart(){

        ArrayList<Image> subSet =  (ArrayList<Image>)allImages.clone();

        Collections.shuffle(subSet);

        Random random = new Random();
        int roundCount = random.nextInt((10 - 5) + 1) + 5;
        list =  new ArrayList<Image>(subSet.subList(0,roundCount));

        like.clear();

        swipe.recycle();
        swipe.loadNewBitmapList(getBitmapList());
        swipe.restart();

        swipeContent.removeView(result);
        swipeContent.addView(swipe);

        buttonsLayout.removeView(restartButton);
        buttonsLayout.addView(noButton);
        buttonsLayout.addView(yesButton);
    }

    public String getQuery(){
        String query = "{\"text\":\"";
        int i;
        for(i=0; i<like.size(); i++) {
            query = query + like.get(i) + " ";
        }
        query = query + "\", \"excluded\":[";
        if(destination.size()>0){
            query = query + "\"" + destination.get(0) + "\"";
        }
        for(i=1; i<destination.size(); i++) {
            query = query + ",\"" + destination.get(i) + "\"";
        }
        query = query + "]}";

        return query;
    }
}
