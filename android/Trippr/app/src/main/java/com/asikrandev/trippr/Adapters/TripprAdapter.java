package com.asikrandev.trippr.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.asikrandev.trippr.CustomViews.TripprSwipeAdapter;
import com.asikrandev.trippr.Entities.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jegasmlm on 8/17/2015.
 */
public class TripprAdapter implements TripprSwipeAdapter {

    private final Context context;
    ArrayList<Image> items;

    public TripprAdapter(Context context, ArrayList<Image> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Image getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public Bitmap getBitmap(int position) {
        if(items.get(position).getBitmap() == null) {
            try {
                FileInputStream fis = context.openFileInput(items.get(position).getSrc());
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }else
            return items.get(position).getBitmap();
    }

    public void setItems(ArrayList<Image> items) {
        this.items = items;
    }

    public void clear() {
        items.clear();
    }
}
