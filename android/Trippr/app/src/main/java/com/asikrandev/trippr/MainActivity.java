package com.asikrandev.trippr;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private FrameLayout content;
    private RelativeLayout buttonsLayout;
    private ImageView image;
    private TextView result;
    private Button restartButton;
    private Button yesButton;
    private Button noButton;

    private ArrayList<Image> list;
    private int position;
    private String tags;

    private AcisionSdk acisionSdk;
    private Messaging messaging;


    private void init(){

        position = 0;
        tags="";

        // delete databases created
        this.deleteDatabase("trippr");
        // creating new database
        MySQLiteHelper db = new MySQLiteHelper(this);

        // add Images
        db.addImage(new Image(1,"img01","peace peaceful lights night city quiet clubs buildings traffic "));
        db.addImage(new Image(2,"img02","work peace wood electronic laptop drink alone yellow table write "));
        db.addImage(new Image(3,"img03","Buildings city landscape colors people metropolis high lakes skyscrapers "));
        db.addImage(new Image(4,"img04","beach sea pier wood landscape sky beautiful  deep hot sunlight "));
        db.addImage(new Image(5,"img05","beach sea pier wood landscape sky beautiful  deep hot sunlight "));

        // get Images from database
        list = db.getAllImages();


        content = (FrameLayout) findViewById(R.id.content);
        buttonsLayout = (RelativeLayout) findViewById(R.id.buttons);
        image = (ImageView) findViewById(R.id.imageView);
        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);

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

        loadImage();

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
                Log.d("trippr", data.getContent());
                result.setText(data.getContent());
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

    public void loadNextImage(){
        position++;
        if(position >= list.size()){
            doSend(tags);

            result.setText("wait...");
            content.removeView(image);
            content.addView(result);

            buttonsLayout.removeView(yesButton);
            buttonsLayout.removeView(noButton);
            buttonsLayout.addView(restartButton);
        }
        else
            loadImage();
    }

    public void loadImage(){
        Log.d("trippr", ""+position);
        Resources res = getResources();
        int resID = res.getIdentifier(list.get(position).getSource(), "drawable", getPackageName());
        Drawable drawable = res.getDrawable(resID );
        image = (ImageView) findViewById(R.id.imageView);
        //send image to the Drawable
        image.setImageDrawable(drawable);
    }

    // Action Buttons
    public void like(View view){
        tags = tags + list.get(position).getTags();
        loadNextImage();
    }

    public void dontLike(View view){
        loadNextImage();
    }

    public void restart(){
        position = 0;
        tags="";

        content.removeView(result);
        content.addView(image);

        buttonsLayout.removeView(restartButton);
        buttonsLayout.addView(yesButton);
        buttonsLayout.addView(noButton);

        loadImage();
    }
}
