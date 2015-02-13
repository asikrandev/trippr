package com.asikrandev.trippr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acision.acisionsdk.AcisionSdk;
import com.acision.acisionsdk.AcisionSdkCallbacks;
import com.acision.acisionsdk.AcisionSdkConfiguration;
import com.acision.acisionsdk.messaging.Messaging;
import com.acision.acisionsdk.messaging.MessagingReceiveCallbacks;
import com.acision.acisionsdk.messaging.MessagingReceivedMessage;
import com.acision.acisionsdk.messaging.MessagingSendOptions;
import com.asikrandev.trippr.util.Image;
import com.asikrandev.trippr.util.MySQLiteHelper;
import com.asikrandev.trippr.util.TripprSwipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private static final int SWIPE_MIN_DISTANCE = 120;

    private FrameLayout content;
    private FrameLayout swipeContent;
    private RelativeLayout buttonsLayout;
    private ImageView image;
    private TextView result;
    private Button restartButton;
    private Button yesButton;
    private Button noButton;

    private ArrayList<Image> list, allImages;
    private ArrayList<String> like, destination;
    private int position;

    private AcisionSdk acisionSdk;
    private Messaging messaging;

    ViewPager.OnTouchListener gestureListener;

    TripprSwipe swipe;

    private void init(){

        like = new ArrayList<String>();
        destination = new ArrayList<String>();

        // delete databases created
        this.deleteDatabase("trippr");
        // creating new database
        MySQLiteHelper db = new MySQLiteHelper(this);

        // add Images
        db.addImage(new Image(1,"img01","peace peaceful lights night city quiet clubs buildings traffic "));
        db.addImage(new Image(2,"img02","peace peaceful sun light clouds air quiet serenity blue colors sunset float smooth sunlight landscape  "));
        db.addImage(new Image(3,"img03","work peace wood electronic laptop drink alone yellow table write "));
        db.addImage(new Image(4,"img04","wood brown hard stuck order old steps  "));
        db.addImage(new Image(5,"img05","Buildings city landscape colors people metropolis high lakes skyscrapers"));
        db.addImage(new Image(6,"img06","road sun desert travel pacific quiet long brown "));
        db.addImage(new Image(7,"img07","light grass quiet colors green sunset sun warm field"));
        db.addImage(new Image(8,"img08","beach sea pier wood landscape sky beautiful  deep hot sunlight "));
        db.addImage(new Image(9,"img09","road long mountains travel rough warm"));
        db.addImage(new Image(10,"img10","rough cars gray hard stuck old brand "));
        db.addImage(new Image(11,"img11","tools work iron steel wood brown dirt "));
        db.addImage(new Image(12,"img12","blue purple lights defocus photography party night clubs"));
        db.addImage(new Image(13,"img13","mess wine drink brands brown box disorder "));
        db.addImage(new Image(14,"img14","yellow blonde quiet trees park sidewalk people walk peaceful wind"));
        db.addImage(new Image(15,"img15","snow white park road winter trees silent alone cold"));
        db.addImage(new Image(16,"img16","city people streets buildings  windows modern old order"));
        db.addImage(new Image(17,"img17","food salad restaurant eat plate delight dinner lunch meat meal"));
        db.addImage(new Image(18,"img18","animals landscape horse green field grass peaceful"));
        db.addImage(new Image(19,"img19","river cascade water jungle mountain high plants trees green white weather "));
        db.addImage(new Image(20,"img20","wood backyard leaves brown wind "));
        db.addImage(new Image(21,"img21","menu restaurants food meals fruits table drink order tea"));
        db.addImage(new Image(22,"img22","road house trees mountain warm sunset west planting farm"));
        db.addImage(new Image(23,"img23","drink money glass ice cold america liquor bar friend"));
        db.addImage(new Image(24,"img24","party colors balloons house neighborhood peace kids cake"));
        db.addImage(new Image(25,"img25","wild tiger yellow look serious jungle zoo animal feline cat"));
        db.addImage(new Image(26,"img26","peaceful night candles light fire yellow steel wish"));
        db.addImage(new Image(27,"img27","party club night drink alcohol lights dance music bar"));
        db.addImage(new Image(28,"img28","sand beach stones brown sun warm"));
        db.addImage(new Image(29,"img29","desert sand hot sun warm alone dust "));
        db.addImage(new Image(30,"img30","beach sand water cold clothes blue salt"));
        db.addImage(new Image(31,"img31","library read book mess study write room music desk"));
        db.addImage(new Image(32,"img32","train wood trees road river lake mountain forest"));
        db.addImage(new Image(33,"img33","stairs castle dark up bricks gray alone"));
        db.addImage(new Image(34,"img34","cars speed numbers drive fuel old classic"));
        db.addImage(new Image(35,"img35","dj party music club dance electro bass night city people enjoy "));
        db.addImage(new Image(36,"img36","history legend town kingdom shield keys armour kings fenix"));
        db.addImage(new Image(37,"img37","egypt pyramid sun hot ocean sea desert landscape"));
        db.addImage(new Image(38,"img38","mountain clouds sky road trees ground town"));
        db.addImage(new Image(39,"img39","history old museum city town wonderful beautiful temple structure architecture"));
        db.addImage(new Image(40,"img40","lake water mountain landscape forest peaceful beautiful sunset sea town"));
        db.addImage(new Image(41,"img41","snow cold white fog ski weather sports clouds high "));
        db.addImage(new Image(42,"img42","vegetables healthy fruit juice home house ingredients food"));
        db.addImage(new Image(43,"img43","food rapid hamburger bread meal meat vegetables cheese delicious flavors"));

        // get Images from database
        allImages = db.getAllImages();

        ArrayList<Image> subSet =  (ArrayList<Image>)allImages.clone();

        Collections.shuffle(subSet);

        list =  new ArrayList<Image>(subSet.subList(0,10));

        swipeContent = (FrameLayout) findViewById(R.id.tripper_swipe);

        buttonsLayout = (RelativeLayout) findViewById(R.id.buttons);

        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);

        swipe = new TripprSwipe(this, getBitmapList());
        swipe.setOnSwipeListener(new TripprSwipe.onSwipeListener() {
            @Override
            public void onLike(int position) {
                like.add(list.get(position).getTags());
            }

            @Override
            public void onFinished() {
                String query = getQuery();

                Log.d("trippr", query);

                doSend(query);

                result.setText("wait...");
                swipeContent.removeView(swipe);
                swipeContent.addView(result);

                buttonsLayout.removeView(yesButton);
                buttonsLayout.removeView(noButton);
                buttonsLayout.addView(restartButton);
            }
        });

        FrameLayout.LayoutParams swipeParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        swipe.setLayoutParams(swipeParams);
        swipeContent.addView(swipe);

        result = new TextView(this);
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        result.setLayoutParams(tvParams);

        restartButton = new Button(this);
        RelativeLayout.LayoutParams buttonParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        restartButton.setLayoutParams(buttonParam);
        restartButton.setText("Restart");
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
        swipe.next();
    }

    public void dontLike(View view){
        swipe.next();
    }

    public void restart(){

        ArrayList<Image> subSet =  (ArrayList<Image>)allImages.clone();

        Collections.shuffle(subSet);

        list =  new ArrayList<Image>(subSet.subList(0,10));

        like.clear();

        swipe.recycle();
        swipe.loadNewBitmapList(getBitmapList());
        swipe.restart();

        swipeContent.removeView(result);
        swipeContent.addView(swipe);

        buttonsLayout.removeView(restartButton);
        buttonsLayout.addView(yesButton);
        buttonsLayout.addView(noButton);
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
