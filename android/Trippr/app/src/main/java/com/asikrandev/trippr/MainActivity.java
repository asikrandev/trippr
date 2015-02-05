package com.asikrandev.trippr;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.asikrandev.trippr.util.Image;
import com.asikrandev.trippr.util.MySQLiteHelper;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // deleting databases created
        //this.deleteDatabase("trippr");
        // creating new database
        MySQLiteHelper db = new MySQLiteHelper(this);

        // add Images
        db.addImage(new Image(1,"img01","peace peaceful lights night city quiet clubs buildings traffic "));
        db.addImage(new Image(1,"img03","work peace wood electronic laptop drink alone yellow table write "));
        db.addImage(new Image(1,"img05","Buildings city landscape colors people metropolis high lakes skyscrapers"));
        db.addImage(new Image(1,"img08","beach sea pier wood landscape sky beautiful  deep hot sunlight "));

        // get Images
        List<Image> list = db.getAllImages();
        Resources res = getResources();
        //Log.d("trippr","HELLO WORLD!");
        //Log.d("trippr",list.get(0).getSource());
        int resID = res.getIdentifier(list.get(0).getSource(), "drawable", getPackageName());
        Drawable drawable = res.getDrawable(resID );
        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageDrawable(drawable);

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
}
