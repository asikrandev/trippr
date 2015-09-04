package com.asikrandev.trippr.CustomViews;

import android.graphics.Bitmap;

/**
 * Created by jegasmlm on 8/17/2015.
 */
public interface TripprSwipeAdapter {

    int getCount();
    Object getItem(int position);
    int getItemId(int position);
    Bitmap getBitmap(int position);

}
