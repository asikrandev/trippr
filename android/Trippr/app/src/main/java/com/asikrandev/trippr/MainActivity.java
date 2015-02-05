package com.asikrandev.trippr;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.asikrandev.trippr.util.Image;
import com.asikrandev.trippr.util.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ImageView image;
    private ArrayList<Image> list;
    private int position;

    private void init(){

        position = 0;

        // delete databases created
        //this.deleteDatabase("trippr");
        // creating new database
        MySQLiteHelper db = new MySQLiteHelper(this);

        // add Images
        db.addImage(new Image(1,"img01","peace peaceful lights night city quiet clubs buildings traffic "));
        db.addImage(new Image(2,"img03","work peace wood electronic laptop drink alone yellow table write "));
        db.addImage(new Image(3,"img05","Buildings city landscape colors people metropolis high lakes skyscrapers"));
        db.addImage(new Image(4,"img08","beach sea pier wood landscape sky beautiful  deep hot sunlight "));

        // get Images from database
        list = db.getAllImages();
        image = (ImageView) findViewById(R.id.imageView);

        loadImage();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

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
        if(position >= list.size())
            position = 0;
        loadImage();

    }

    public void loadImage(){

        Resources res = getResources();
        int resID = res.getIdentifier(list.get(position).getSource(), "drawable", getPackageName());
        Drawable drawable = res.getDrawable(resID );
        image = (ImageView) findViewById(R.id.imageView);
        //send image to the Drawable
        image.setImageDrawable(drawable);

    }

    // Action Buttons
    public void next(View view){

        loadNextImage();

    }
}
